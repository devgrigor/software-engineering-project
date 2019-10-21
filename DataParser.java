import java.io.File;

import net.sourceforge.tess4j.*;

public class DataParser {

    private static String language = "";
    private String PATH = "TrainData";
    private ITesseract instance;


    public void setLanguage(String lang) {
        language = lang;
        instance.setLanguage(language);
    }

    public void setPath(String path) {
        PATH = path;
        instance.setDatapath(PATH);
    }

    public DataParser() {
        instance = new Tesseract();


    }

    public String recognize(File file) {
        String text = "Unable to recognize text";
        try {
            Object result = instance.doOCR(file);
            text = result.toString();
        } catch (TesseractException e) {
            e.getMessage();
        }
        return text;
    }

}

