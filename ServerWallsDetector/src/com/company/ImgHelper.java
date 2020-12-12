package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;

public class ImgHelper extends JComponent {

    public static BufferedImage FindWalls(BufferedImage img){
        return img;
    }

    public static void SaveImg(BufferedImage img, String path){
        if(img != null){
            try {
                File outputfile = new File(path);
                ImageIO.write(img, "jpg", outputfile);
                System.out.println("Image saved: " + path);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }
}
