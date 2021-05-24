package com.example.wallsdetector.ClientSocket;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MobSocket {

    private String TAG_LOG = "info";

    private int BUFER_SIZE = 1024*2;
    private Socket socket;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private byte[] buffer = new byte[BUFER_SIZE];

    public boolean isConnected(){
        return socket.isConnected();
    }

    private MobSocket(){
        socket = new Socket();
    }

    private static MobSocket instance;

    public static MobSocket getInstance(){
        return (instance == null) ? instance = new MobSocket() : instance;
    }

    public boolean unconnectToDevice() {
        try
        {
            if(dataInputStream != null) dataInputStream.close();
            Log.d(TAG_LOG, "dataInputStream close");
            if(dataOutputStream != null) dataOutputStream.close();
            Log.d(TAG_LOG, "dataOutputStream close");
            if(socket != null) socket.close();
            Log.d(TAG_LOG, "Soket close");

            instance = null;
            return true;

        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    public void connectToDevice(String ip, int port) {
        try
        {
            Log.d(TAG_LOG, "start created new soket object");
            //socket.setSoTimeout(50000);
            socket.connect(new InetSocketAddress(ip,port));
            Log.d(TAG_LOG, "new Soket object");

        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            instance = null;
        } catch (IOException e) {
            e.printStackTrace();
            instance = null;
        }

    }

    public boolean sendMessage(String msg) {
        try
        {
            if(dataOutputStream == null){
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                Log.d(TAG_LOG, "output stream created");
            }

            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();

            Log.d(TAG_LOG, "output stream worked");
            return true;

        }
        catch (IOException e)
        { e.printStackTrace(); }

        return false;
    }

    public boolean sendMessage(int msg) {
        try
        {
            if(dataOutputStream == null){
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                Log.d(TAG_LOG, "output stream created");
            }

            dataOutputStream.writeInt(msg);
            dataOutputStream.flush();

            Log.d(TAG_LOG, "output stream worked");
            return true;

        }
        catch (IOException e)
        { e.printStackTrace(); }

        return false;
    }

    public String getMessage() {
        String s = "";
        try
        {
            if(dataInputStream == null){
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                Log.d(TAG_LOG, "input stream created");
            }

            s = dataInputStream.readUTF();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (IOException e)
        { e.printStackTrace(); }

        Log.d(TAG_LOG, "input stream worked");
        return s;
    }

    public void sendFile(byte[] bytes) {
        try
        {
            if(dataOutputStream == null){
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                Log.d(TAG_LOG, "output stream created");
            }

            dataOutputStream.writeInt(bytes.length);
            dataOutputStream.write(bytes,0,bytes.length);
            dataOutputStream.flush();
        }
        catch (IOException e)
        { e.printStackTrace(); }

        Log.d(TAG_LOG, "output stream worked");
    }

    public byte[] getFile() {
        byte[] result = new byte[1];
        try
        {
            if(dataInputStream == null){
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                Log.d(TAG_LOG, "input stream created");
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int current_size = 0;
            int size = 0;
            int length = 0;

            // получение размера принимаемого масиива
            size=  dataInputStream.readInt();
            Log.d(TAG_LOG, "future size: " + size);

            // приём самого массива частями
            do{
                length = dataInputStream.read(buffer);
                byteArrayOutputStream.write(buffer, 0, length);
                current_size += length;
                Log.d(TAG_LOG, length + "\t | " + current_size + "\t | " + size);
            } while(current_size < size);

            result = byteArrayOutputStream.toByteArray();

            Log.d(TAG_LOG, "current size: " + result.length);
            byteArrayOutputStream.close();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (IOException e)
        { e.printStackTrace(); }

        Log.d(TAG_LOG, "input stream worked");
        return result;
    }
}
