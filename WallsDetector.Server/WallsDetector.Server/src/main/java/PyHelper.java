import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PyHelper {

    public static void CallPy(String command) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(command);

        InputStream stdout = p.getInputStream();
        InputStream stderr = p.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stdout);
        InputStreamReader isrerr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);
        BufferedReader brerr = new BufferedReader(isrerr);

        String line = null;

        System.out.println("OUTPUT:");
        while ((line = br.readLine()) != null)
            System.out.println(line);
        System.out.println();

        System.out.println("ERROR:");
        while ((line = brerr.readLine()) != null)
            System.out.println(line);
        System.out.println();

        p.waitFor();
    }

}
