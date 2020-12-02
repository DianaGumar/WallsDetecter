package com.example.wallsdetector.ClientSocket;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

//singletone
public class MobSocket {

    private  String   TAG_LOG = "info";

    private Socket socket;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private byte[] buffer = new byte[1024];

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

    public boolean sendMessage(String str)
    {
        try
        {
            if(dataOutputStream == null){
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                Log.d(TAG_LOG, "output stream created");
            }
            dataOutputStream.write(str.getBytes());
//            dataOutputStream.writeUTF(str);
            dataOutputStream.flush();

            Log.d(TAG_LOG, "output stream worked");
            return true;

        }
        catch (IOException e)
        { e.printStackTrace(); }

        return false;
    }

    public String getMessage()
    {
        String s = null;

        try
        {
            if(dataInputStream == null){
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                Log.d(TAG_LOG, "input stream created");
            }

            int length = dataInputStream.read(buffer);

            if (length != -1){
                s = new String(buffer, 0, length);
            }

            Log.d(TAG_LOG, "input stream worked");
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        catch (IOException e)
        { e.printStackTrace(); }

        return s;
    }



}
