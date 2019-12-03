import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * A class that prepossesses the input, to make the output result better
 */
public class Preprocessor {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
        return bi;
    }

    public String preprocess(File file) throws Exception {
        String path = file.getAbsolutePath();
        String nativeDir = path.substring(0, path.lastIndexOf(File.separator));
        String resultingpath = nativeDir + "\\temporary_image_ocreate." + getFileExtension(file);
        System.out.println(resultingpath);
        Mat imgGray = new Mat();
        Mat imgGaussianBlur = new Mat();
        Mat imgAdaptiveThreshold = new Mat();

        Mat img = Imgcodecs.imread(file.getAbsolutePath());
        System.out.println("OpenCV Mat data:\n" + img);

        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imgGray, imgGaussianBlur, new Size(3, 3), 0);
        Imgproc.adaptiveThreshold(imgGaussianBlur, imgAdaptiveThreshold, 255, 1, 1, 75, 35);

        BufferedImage bi = Mat2BufferedImage(imgAdaptiveThreshold);

        BufferedImage imageCopy = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        imageCopy.getGraphics().drawImage(bi, 0, 0, null);
        byte[] data = ((DataBufferByte) imageCopy.getRaster().getDataBuffer()).getData();
        Mat newimg = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        newimg.put(0, 0, data);

        String fileName = file.getName();
        Imgcodecs.imwrite(resultingpath, newimg);
        return resultingpath;
    }

    public String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        String ext = name.substring(lastIndexOf);
        return ext.substring(1);
    }

    public String imageToFile(BufferedImage bi, String extension, String path) throws IOException {
        try {
            File outputfile = new File(path + "temporary_image_ocreate." + extension);
            ImageIO.write(bi, "extension", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "done";
    }

    public boolean deleteTemp(String path) throws IOException {
        if (!path.equals("")) {
            File fileToDelete = FileUtils.getFile(path);
            return FileUtils.deleteQuietly(fileToDelete);
        }
        return false;
    }
}
