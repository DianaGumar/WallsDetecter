package com.example.wallsdetector.ClientSocket;

import android.os.AsyncTask;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.Arrays;

public class MobTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String ... params) {
        try {
            MobSocket mobSocket = MobSocket.getInstance();
            mobSocket.connectToDevice(params[0], Integer.parseInt(params[1]));

            if(mobSocket.isConnected()){
                String messageFromServer = mobSocket.getMessage();

                Log.d("info", "server msg: " + messageFromServer);
                Log.d("info", "connect successful");
                return true;

            }else {
                Log.d("info", "Can't connected");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}