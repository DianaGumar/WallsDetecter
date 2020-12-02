package com.example.wallsdetector.ClientSocket;

import android.os.AsyncTask;
import android.util.Log;

public class MobTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String ... params) {
        try {
            MobSocket mobSocket = MobSocket.getInstance();
            mobSocket.connectToDevice(params[0], Integer.parseInt(params[1]));

            if(mobSocket.isConnected()){
                String messageFromServer = mobSocket.getMessage();

                if(messageFromServer != null){
                    Log.d("info", "connect successful");
                    return true;
                }

            }else {
                Log.d("info", "Can't connected");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}