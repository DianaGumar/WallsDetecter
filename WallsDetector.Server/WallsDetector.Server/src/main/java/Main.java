import java.io.IOException;

public class Main {
    public static void main (String[] args) throws IOException {
        nu.pattern.OpenCV.loadShared();
        //System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);

        Server server = new Server();
        server.Start();
    }
}
