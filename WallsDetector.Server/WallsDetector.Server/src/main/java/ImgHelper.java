//import org.opencv.core.Mat;
//import org.opencv.core.MatOfByte;
//import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImgHelper extends JComponent {

    public static void SaveImg(BufferedImage img, String path) throws IOException {
        if(img != null){
            File outputfile = new File(path);
            ImageIO.write(img, "jpg", outputfile);
        }
    }

    public static BufferedImage ReadImg(String path) throws IOException {
        File outputfile = new File(path);
        return ImageIO.read(outputfile);
    }

//    public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(image, "jpg", byteArrayOutputStream);
//        byteArrayOutputStream.flush();
//        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
//    }
//
//    public static BufferedImage Mat2BufferedImage(Mat matrix)throws IOException {
//        MatOfByte mob=new MatOfByte();
//        Imgcodecs.imencode(".jpg", matrix, mob);
//        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
//    }

    public static BufferedImage FindWalls(BufferedImage source) throws IOException, InterruptedException {

//        Mat image =  BufferedImage2Mat(source);
//        //image = Imgcodecs.imread("./original.jpg");
//        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
//        //Imgcodecs.imwrite("./edited.jpg", image);
//        Mat2BufferedImage(image);

        // python делает работу за java
        ImgHelper.SaveImg(source, "./original.jpg");
        PyHelper.CallPy("python ./wd.py \"original.jpg\" \"edited.jpg\"");
        return ImgHelper.ReadImg("./edited.jpg");
    }
}
