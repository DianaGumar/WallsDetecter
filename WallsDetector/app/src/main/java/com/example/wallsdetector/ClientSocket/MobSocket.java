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

public class MobSocket {

    private String TAG_LOG = "info";

    private int BUFER_SIZE = 1024;
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


    public boolean unconnectToDevice()
    {
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

    public void connectToDevice(String ip, int port)
    {
        try
        {
            Log.d(TAG_LOG, "start created new soket object");
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
            //DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            if(dataOutputStream == null){
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                Log.d(TAG_LOG, "output stream created");
            }

            dataOutputStream.write(msg.getBytes());
//            dataOutputStream.writeUTF(str);
            dataOutputStream.flush();

            Log.d(TAG_LOG, "output stream worked");
            return true;

        }
        catch (IOException e)
        { e.printStackTrace(); }

        return false;
    }

    public boolean sendMessage(byte[] bytes) {
        try
        {
            //DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            if(dataOutputStream == null){
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                Log.d(TAG_LOG, "output stream created");
            }
            int size = bytes.length;

            dataOutputStream.write(String.valueOf(size).getBytes());
            dataOutputStream.write(bytes,0,size);
//            dataOutputStream.writeUTF(str);
            dataOutputStream.flush();

            Log.d(TAG_LOG, "output stream worked");
            return true;

        }
        catch (IOException e)
        { e.printStackTrace(); }

        return false;
    }

    public byte[] getMessage() {

        byte[] result = new byte[1];

        try
        {
            //DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if(dataInputStream == null){
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                Log.d(TAG_LOG, "input stream created");
            }

            int length = 0;
            do{
                length = dataInputStream.read(buffer);
                byteArrayOutputStream.write(buffer, 0, length);

            } while(length >= BUFER_SIZE);

            Log.d(TAG_LOG, "input stream worked");
            result = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (IOException e)
        { e.printStackTrace(); }

        return result;
    }
}
