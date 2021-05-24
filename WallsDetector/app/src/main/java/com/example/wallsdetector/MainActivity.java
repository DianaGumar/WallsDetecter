package com.example.wallsdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wallsdetector.ClientSocket.MobSocket;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private Button b_enter;
    private EditText port;
    private EditText ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b_enter = findViewById(R.id.b_connect);
        port = findViewById(R.id.car_port);
        ip = findViewById(R.id.car_ip);

        b_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String p = port.getText().toString();
                String ip_str = ip.getText().toString();

                MobTask mobTask = new MobTask();
                mobTask.execute(ip_str, p);
            }
        });
    }


    private class MobTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            b_enter.setEnabled(false);
        }

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

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            b_enter.setEnabled(true);

            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Connected to server =)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Can't connected to server =(", Toast.LENGTH_SHORT).show();
            }

            if(aBoolean){
                Intent intent2 = new Intent(getApplicationContext(), WorkActivity.class);
                startActivity(intent2);
            }
        }

    }

}
