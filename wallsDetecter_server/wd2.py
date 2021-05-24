#!/usr/bin/env python
# coding: utf-8

# In[41]:

import torch
import torch.nn as nn
import torch.optim as optim

from mit_semseg.config import cfg
from mit_semseg.dataset import TestDataset
from mit_semseg.models import ModelBuilder, SegmentationModule
from mit_semseg.utils import colorEncode

from torch.utils.mobile_optimizer import optimize_for_mobile

# System libs
import os, csv, torch, scipy.io, PIL.Image, torchvision.transforms
import numpy as np
import cv2
import sys


def LookForWall(segmentation_module, colors, img, color, size_a):
    img_original, img_data = NormalizeImg(img.copy(), size_a)
    singleton_batch = {'img_data': img_data[None]}
    output_size = img_data.shape[1:]
    
    with torch.no_grad():
        scores = segmentation_module(singleton_batch, segSize=output_size)
    # Get the predicted scores for each pixel
    _, pred = torch.max(scores, dim=1)
    pred = pred.cpu()[0].numpy()
    pred[pred != 0] = -1
    
    # colorize prediction
    pred_color = colorEncode(pred, colors).astype(np.uint8)
    # # aggregate images
    # im_vis = np.concatenate((img_original, pred_color), axis=1)
    # display(PIL.Image.fromarray(im_vis))
    return img_original, pred_color
    
def ConcatenateImgsWithAlpha(img, img2, color, alpha1, alpha2):
    img_real = img.copy()
    img_real_mask = img.copy()
    img_mask = img2.copy()

    img_real_mask = AddAlpha(img_real_mask)
    img_mask = AddAlpha(img_mask)
    img_real = AddAlpha(img_real)
    # Make mask of black pixels - mask is True where image is black
    mBlack = (img_mask[:, :, 0:3] == [0,0,0]).all(2)
    img_real_mask[mBlack == False] = (color[0], color[1], color[2], 255)

    img_real = cv2.addWeighted(img_real_mask,alpha1,img_real,alpha2,0)

    return img_real

def AddAlpha(img):
    h, w = img.shape[:2]
    # Add an alpha channel, fully opaque (255)
    RGBA = np.dstack((img, np.zeros((h,w),dtype=np.uint8)+255))
    
    return RGBA
    
# принимает rqb массив
def MakeAlfaFromColor(img, color):
    # Make into Numpy array of RGB and get dimensions
    RGB = img.copy()
    h, w = RGB.shape[:2]
    # Add an alpha channel, fully opaque (255)
    RGBA = np.dstack((RGB, np.zeros((h,w),dtype=np.uint8)+255))
    # Make mask of black pixels - mask is True where image is black
    mBlack = (RGBA[:, :, 0:3] == color).all(2)
    # Make all pixels matched by mask into transparent ones
    RGBA[mBlack] = (0,0,0,0)
    
    return RGBA

def ChangeColor(img, originalValue, wantValue):
    im = img.copy()
    data = np.array(im)
    r1, g1, b1 = originalValue[0], originalValue[1], originalValue[2] # Original value
    r2, g2, b2 = wantValue[0], wantValue[1], wantValue[2] # Value that we want to replace it with 204, 49, 49
    red, green, blue = data[:,:,0], data[:,:,1], data[:,:,2]
    mask = (red == r1) & (green == g1) & (blue == b1)
    data[:,:,:3][mask] = [r2, g2, b2]
    
    return data

def LoadModel():
    # загрузка декодера и енкодера
    name_model = "ade20k-mobilenetv2dilated-c1_deepsup"
    # Network Builders net_encoder net_decoder
    net_encoder = ModelBuilder.build_encoder(
        arch="mobilenetv2dilated",
        fc_dim=320,
        weights=name_model+'/encoder_epoch_20.pth')
    net_decoder = ModelBuilder.build_decoder(
        arch="c1_deepsup",
        fc_dim=320,
        num_class=150,
        weights=name_model+'/decoder_epoch_20.pth',
        use_softmax=True)
    # построение модели
    crit = torch.nn.NLLLoss(ignore_index=-1)
    segmentation_module = SegmentationModule(net_encoder, net_decoder, crit)
    segmentation_module.eval()
    
    return segmentation_module

def NormalizeImg(img, size_f):
    size_l = size_f, size_f
    pil_image = img
    pil_image.thumbnail(size_l, PIL.Image.ANTIALIAS)
    pil_image.convert("RGB")

    # Load and normalize one image as a singleton tensor batch
    pil_to_tensor = torchvision.transforms.Compose([
        torchvision.transforms.ToTensor(),
        torchvision.transforms.Normalize(
            mean=[0.485, 0.456, 0.406], # These are RGB mean+std values
            std=[0.229, 0.224, 0.225])  # across a large photo dataset.
    ])
    img_original = np.array(pil_image)
    img_data = pil_to_tensor(pil_image)

    return img_original, img_data

def colorEncode(labelmap, colors, mode='RGB'):
    labelmap = labelmap.astype('int')
    labelmap_rgb = np.zeros((labelmap.shape[0], labelmap.shape[1], 3),
                            dtype=np.uint8)
    for label in unique(labelmap):
        if label < 0:
            continue
        labelmap_rgb += (labelmap == label)[:, :, np.newaxis] *             np.tile(colors[label],
                    (labelmap.shape[0], labelmap.shape[1], 1))

    if mode == 'BGR':
        return labelmap_rgb[:, :, ::-1]
    else:
        return labelmap_rgb
    
def unique(ar, return_index=False, return_inverse=False, return_counts=False):
    ar = np.asanyarray(ar).flatten()

    optional_indices = return_index or return_inverse
    optional_returns = optional_indices or return_counts

    if ar.size == 0:
        if not optional_returns:
            ret = ar
        else:
            ret = (ar,)
            if return_index:
                ret += (np.empty(0, np.bool),)
            if return_inverse:
                ret += (np.empty(0, np.bool),)
            if return_counts:
                ret += (np.empty(0, np.intp),)
        return ret
    if optional_indices:
        perm = ar.argsort(kind='mergesort' if return_index else 'quicksort')
        aux = ar[perm]
    else:
        ar.sort()
        aux = ar
    flag = np.concatenate(([True], aux[1:] != aux[:-1]))

    if not optional_returns:
        ret = aux[flag]
    else:
        ret = (aux[flag],)
        if return_index:
            ret += (perm[flag],)
        if return_inverse:
            iflag = np.cumsum(flag) - 1
            inv_idx = np.empty(ar.shape, dtype=np.intp)
            inv_idx[perm] = iflag
            ret += (inv_idx,)
        if return_counts:
            idx = np.concatenate(np.nonzero(flag) + ([ar.size],))
            ret += (np.diff(idx),)
    return ret


if __name__ == "__main__": #sys.argv[3]
    segmentation_module = LoadModel()
    colors = scipy.io.loadmat(sys.argv[3])['colors']
    img = PIL.Image.open(sys.argv[1])
    im2 = np.array(img.copy())
    x, y = im2.shape[0], im2.shape[1]
    img_original, pred_img = LookForWall(segmentation_module, colors, img, [71, 33, 207], 310)
    # масштаб маски до реальных размеров
    pred_img = cv2.resize(pred_img, dsize=(y, x), interpolation=cv2.INTER_CUBIC)
    img_original_2 = ConcatenateImgsWithAlpha(im2, pred_img, [71, 33, 207], 0.7, 0.3)   
    img_original_2 = PIL.Image.fromarray(img_original_2)
    img_original_2 = img_original_2.convert('RGB')
    img_original_2.save(sys.argv[2])



