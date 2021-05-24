import java.io.IOException;

public class Program {
    public static void main (String[] args) throws IOException {
        // nu.pattern.OpenCV.loadShared();
        Server server = new Server();
        server.Start();
    }
}
