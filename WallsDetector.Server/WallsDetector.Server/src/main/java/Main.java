import java.io.IOException;

public class Main {
    public static void main (String[] args) throws IOException, InterruptedException {
//        nu.pattern.OpenCV.loadShared();

        Server server = new Server();
        server.Start();
    }
}
