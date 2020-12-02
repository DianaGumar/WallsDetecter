package com.company;

import com.sun.xml.internal.ws.resources.SenderMessages;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int SERVER_PORT = 8086;

    private ServerSocket server;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public void Start() throws IOException {

        server = new ServerSocket(SERVER_PORT);

        System.out.println(server.getLocalSocketAddress());
        System.out.println("Your current IP address : " + InetAddress.getLocalHost());

        while(true){
            final Socket socket = server.accept();

            executor.execute(new Runnable() {

                public void run() {
                    try {
                        handleSocket(socket);
                    } catch (IOException e) {
                        System.out.println("handle exeption " + e.getMessage());
                    }
                }
                private void handleSocket(final Socket socket) throws IOException {
                    DataOutputStream dataOutputStream = null;
                    DataInputStream dataInputStream = null;
                    byte[] buffer = new byte[1024];

                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dataOutputStream.write("Success connected!".getBytes());
                    dataOutputStream.flush();

                    dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    int i = dataInputStream.read(buffer);

                    int client_type = 0;
                    if(i != -1){
                        String message = new String(buffer, StandardCharsets.UTF_8);
                        client_type = Integer.parseInt(message.trim());
                        System.out.println("client start work. " + client_type);

                        if(client_type == 1){
                            WorkWithFirstClientType(dataOutputStream, dataInputStream, buffer);
                        }
                        else if(client_type == 2){
                            WorkWithSecondClientType(dataOutputStream, dataInputStream);
                        }
                        else{
                            System.out.println("client type wasn't recognized: " + client_type);
                        }
                    }

                    System.out.println("client ended work. " + client_type);

                    dataInputStream.close();
                    dataOutputStream.close();
                    socket.close();
                }
            });
        }
    }

    private void WorkWithFirstClientType(DataOutputStream dataOutputStream, DataInputStream dataInputStream,
                                         byte[] buffer) throws IOException {
        String message = "";

        while(true){
            int length = dataInputStream.read(buffer);
            if(length != -1){
                message = new String(buffer, 0, length);
                if(message.equals("0")) break;

                System.out.println("First client send: " + message);

                message = "ansver from server: + " + message;
                dataOutputStream.write(message.getBytes());
                dataOutputStream.flush();
            }
            else break;
        }
    }

    private void WorkWithSecondClientType(DataOutputStream dataOutputStream, DataInputStream dataInputStream){
        System.out.println("work with second client doesnt implement");
    }

}