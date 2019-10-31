import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;

public class ParserTest {

    @Test
    public void testRecognize() {
    	ClassLoader classLoader = getClass().getClassLoader();
        DataParser dp = new DataParser("eng");
       	File file = new File("tests/test-musterpoint.png");
        System.out.println(file);
        String res = dp.recognize(file);

        assertEquals("MusterPoint\n", res);

    }
}
