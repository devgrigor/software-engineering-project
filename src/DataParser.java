import java.io.File;

import net.sourceforge.tess4j.*;

public class DataParser {

    private static String language = "eng";
    private String PATH = "tessdata";
    private ITesseract instance;

    public DataParser() {
        this(language);
    }

    public DataParser(String language) {
        instance = new Tesseract();
        instance.setDatapath(PATH);
        instance.setLanguage(language);
    }

    public String recognize(File file) {
        String text = "";
        try {
            Object result = instance.doOCR(file);
            text = result.toString();
        } catch (TesseractException e) {
            e.getMessage();
        }
        return text;
    }

}
