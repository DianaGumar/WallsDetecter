import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int SERVER_PORT = 8086;
    private static final int BUFER_SIZE = 1024*2;

    private ServerSocket server;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public void Start() throws IOException {

        server = new ServerSocket(SERVER_PORT);

        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Current IP address (look for ipconfig).. : " + ip.getHostAddress());
        System.out.println("Current port : " + SERVER_PORT);

        while(true){
            final Socket socket = server.accept();

            executor.execute(new Runnable() {

                public void run() {
                    try {
                        handleSocket(socket);
                    } catch (IOException e) {
                        System.out.println("handle exeption " + e.getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                private void handleSocket(final Socket socket) throws IOException, InterruptedException {
                    byte[] buffer = new byte[BUFER_SIZE];

                    DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dataOutputStream.writeUTF("Success connected!");
                    dataOutputStream.flush();

                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    int client_type = dataInputStream.readInt();

                    System.out.println("client start work. " + client_type);

                    if(client_type == 1){
                        WorkWithFirstClientType(dataOutputStream, dataInputStream, buffer);
                    }
                    else if(client_type == 2){
                        WorkWithSecondClientType(dataOutputStream, dataInputStream, buffer);
                    }
                    else{
                        System.out.println("client type wasn't recognized: " + client_type);
                    }

                    System.out.println("client ended work. " + client_type);
                    dataOutputStream.close();
                    dataInputStream.close();
                    socket.close();
                }
            });
        }
    }

    private void WorkWithFirstClientType(DataOutputStream dataOutputStream,
                                         DataInputStream dataInputStream, byte[] buffer)
            throws IOException, InterruptedException {

        while(true){
            int length = 0;
            int current_size = 0;
            int size = 0;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // получение размера принимаемого масиива
            size = dataInputStream.readInt();
            if(size < 1) {
                break;
            }
            System.out.println("future img size: " + size);

            // приём самого массива частями
            System.out.println("got\t | all_got\t | need_all_got");
            do{
                length = dataInputStream.read(buffer);
                byteArrayOutputStream.write(buffer, 0, length);
                current_size += length;
                System.out.print("\r" + length + "\t | " + current_size + "\t | " + size);

            } while(current_size < size);

            if(length < 2) {
                break;
            }

            byte[] b_img = byteArrayOutputStream.toByteArray();
            System.out.println("\nimg size: " + b_img.length);
            ByteArrayInputStream bais = new ByteArrayInputStream(b_img);
            BufferedImage img = ImageIO.read(bais);
            bais.close();

            // обработка картинки
            System.out.println("\nlooking for wallses borders..");
            BufferedImage new_img = ImgHelper.FindWalls(img);

            // отправка обработанного изображения
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( new_img, "jpg", baos );
                baos.flush();
                byte[] new_b_img = baos.toByteArray();
                baos.close();

                System.out.println("new img size: " + new_b_img.length);
                dataOutputStream.writeInt(new_b_img.length);

                dataOutputStream.write(new_b_img, 0, new_b_img.length);
                dataOutputStream.flush();

            }catch (Exception e) {
                System.out.println("error: " + e.getMessage());
            }

            byteArrayOutputStream.close();
        }

        dataOutputStream.close();
        dataInputStream.close();
    }

    private void WorkWithSecondClientType(DataOutputStream dataOutputStream,
                                          DataInputStream dataInputStream, byte[] buffer){
        System.out.println("work with second client doesnt implement");
    }

}