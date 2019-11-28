import java.io.*;

public class ExportModule {

    public boolean exportFile(String text, String name, String format, String path) {
        boolean success = false;

        if (!format.equals("doc") && !format.equals("txt")) {
            return success;
        }

        String pathname = path;
        File file = new File(pathname);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(text);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
                return success;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

}

