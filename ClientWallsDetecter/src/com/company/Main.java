package com.company;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    private static Socket socket;
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) throws IOException, InterruptedException {
	// write your code here

        socket = new Socket();

        Scanner in = new Scanner(System.in);
        System.out.print("Enter ip: ");
        String ip = in.nextLine();
        int port = 8086;

        socket.connect(new InetSocketAddress(ip,port));
        System.out.println("Connected!");

        String ms = getMessage();
        System.out.println("server ms: " + ms);
        sendMessage("2");

        TimeUnit.SECONDS.sleep(2);

        sendMessage("hiiie");
        ms = getMessage();
        System.out.println("server ms: " + ms);

        sendMessage("0");

        dataInputStream.close();
        dataOutputStream.close();
        socket.close();
    }

    public static boolean sendMessage(String str)
    {
        try
        {
            if(dataOutputStream == null){
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            }
            dataOutputStream.write(str.getBytes());
//            dataOutputStream.writeUTF(str);
            dataOutputStream.flush();

            return true;

        }
        catch (IOException e)
        { e.printStackTrace(); }

        return false;
    }

    public static String getMessage()
    {
        String s = null;

        try
        {
            if(dataInputStream == null){
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            }

            byte[] b = new byte[45];
            int i = dataInputStream.read(b);

            if(i > 0){
                s = new String(b);
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (IOException e)
        { e.printStackTrace(); }

        return s;
    }




}
