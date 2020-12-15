#!/usr/bin/env python
# coding: utf-8

# In[2]:


import numpy as np
import cv2 as cv
import pandas as pd
import matplotlib.pyplot as plt

from mpl_toolkits.mplot3d import Axes3D
from matplotlib import cm
from matplotlib import colors

from matplotlib.colors import hsv_to_rgb
import math
from scipy.interpolate import splprep, splev


# In[3]:


def GetCounters(im_thresh, approx_coef):
    contours, hierarchy = cv.findContours(im_thresh.copy(), cv.RETR_CCOMP, cv.CHAIN_APPROX_NONE)
    # вычисляем площади замкнутых контуров, 
    # для отсеивания мусора
    areas = []
    for i in contours:
        areas.append(cv.contourArea (i))
    areas_average = sum(areas) / len(areas)
    # убираем небольшие контура
    contours_new = []
    for i in contours:
        if cv.contourArea(i) > areas_average :
            contours_new.append(i)   
    #уменьшение количества точек и аппроксимация контуров
    contours_appr = contours_new.copy()
    for i in range(len(contours_new)):
        epsilon = approx_coef*cv.arcLength(contours_new[i], True)
        approx = cv.approxPolyDP(contours_new[i], epsilon, True)
        contours_appr[i] = approx
    
    return contours_appr


# In[10]:


def drowDots(dots, img, color2):
    if dots is not None:
        for i in range(0, len(dots)):
            cv.circle(img, (dots[i][0][0],dots[i][0][1]), radius=2, color=color2, thickness=-1)
    return img


# In[5]:


def drowLine(lines, img, color):
    if lines is not None:
        for i in range(0, len(lines)):
            rho = lines[i][0][0] 
            theta = lines[i][0][1]
            a = math.cos(theta)
            b = math.sin(theta) 
            x0 = a * rho
            y0 = b * rho
            pt1 = (int(x0 + 1000*(-b)), int(y0 + 1000*(a)))
            pt2 = (int(x0 - 1000*(-b)), int(y0 - 1000*(a)))
            cv.line(img, pt1, pt2, color, 1, cv.LINE_4)
    return img


# In[6]:


from collections import defaultdict

def segment_by_angle_kmeans(lines, k=2, **kwargs):
    # Define criteria = (type, max_iter, epsilon)
    default_criteria_type = cv.TERM_CRITERIA_EPS + cv.TERM_CRITERIA_MAX_ITER
    criteria = kwargs.get('criteria', (default_criteria_type, 10, 1.0))
    flags = kwargs.get('flags', cv.KMEANS_RANDOM_CENTERS)
    attempts = kwargs.get('attempts', 10)

    # returns angles in [0, pi] in radians
    angles = np.array([line[0][1] for line in lines])
    # multiply the angles by two and find coordinates of that angle
    pts = np.array([[np.cos(2*angle), np.sin(2*angle)]
                    for angle in angles], dtype=np.float32)

    # run kmeans on the coords
    labels, centers = cv.kmeans(pts, k, None, criteria, attempts, flags)[1:]
    labels = labels.reshape(-1)  # transpose to row vec

    # segment lines based on their kmeans label
    segmented = defaultdict(list)
    for i, line in zip(range(len(lines)), lines):
        segmented[labels[i]].append(line)
    segmented = list(segmented.values())
    return segmented


# In[7]:


def intersection(line1, line2):
    rho1, theta1 = line1[0]
    rho2, theta2 = line2[0]
    A = np.array([
        [np.cos(theta1), np.sin(theta1)],
        [np.cos(theta2), np.sin(theta2)]
    ])
    b = np.array([[rho1], [rho2]])
    x0, y0 = np.linalg.solve(A, b)
    x0, y0 = int(np.round(x0)), int(np.round(y0))
    return [[x0, y0]]


def segmented_intersections(lines):
    intersections = []
    for i, group in enumerate(lines[:-1]):
        for next_group in lines[i+1:]:
            for line1 in group:
                for line2 in next_group:
                    intersections.append(intersection(line1, line2)) 

    return intersections


# In[8]:


def wallsFinder(img_path_in, img_path_out):
    src2 = cv.imread(img_path_in, 1)
    
    dst = cv.Canny(src2.copy(), 0, 255, None, 3)
    h, w, c = src2.shape
    blank_image = np.zeros((h,w,1), np.uint8)
    counters = GetCounters(dst, 0.001)
    dst = cv.drawContours(blank_image.copy(), counters, -1, (255, 255, 255), 3, cv.LINE_4, None, 1)
    dst = cv.copyMakeBorder(dst, top=2, bottom=2, left=2, right=2, borderType= cv.BORDER_CONSTANT, value=[255,255,255] )
    
    dst = cv.bitwise_not(dst)
    dst = cv.Canny(dst, 0, 255, None, 3)
    
    cdst = cv.cvtColor(dst, cv.COLOR_GRAY2BGR)
    cdstP = np.copy(cdst)
    lines = cv.HoughLines(dst, 1, np.pi / 180, 150, None, 0, 0)
    cdst = drowLine(lines, cdst, (0,0,255))
#     linesP = cv.HoughLinesP(dst, 1, np.pi / 180, 50, None, 50, 10)
#     if linesP is not None:
#         for i in range(0, len(linesP)):
#             l = linesP[i][0]
#             cv.line(cdstP, (l[0], l[1]), (l[2], l[3]), (0,0,255), 1, cv.LINE_4)

    segmented = segment_by_angle_kmeans(lines)
    intersections = segmented_intersections(segmented)
    img = src2.copy()
    drowLine(segmented[0], img, (0,0,255))
    drowLine(segmented[1], img, (255,0,0))
    drowDots(intersections, img, (0,255,255))
    
    cv.imwrite(img_path_out, img) 
    
def wallsFinderSimple(img_path_in, img_path_out):
    src2 = cv.imread(img_path_in, 1)

    dst = cv.Canny(src2.copy(), 0, 255, None, 3)
    h, w, c = src2.shape
    blank_image = np.zeros((h,w,1), np.uint8)
    counters = GetCounters(dst, 0.002)
    dst = cv.drawContours(src2.copy(), counters, -1, (255, 0, 0), 1, cv.LINE_4, None, 1)

    cv.imwrite(img_path_out, dst)


# In[11]:


import sys
if __name__ == "__main__":
    wallsFinder(sys.argv[1], sys.argv[2])
   # wallsFinderSimple(sys.argv[1], sys.argv[2])
#     wallsFinder("./2.jpg", "./22.jpg")


# In[ ]:




