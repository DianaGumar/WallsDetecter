package com.example.wallsdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wallsdetector.ClientSocket.MobTask;

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

                boolean connected = false;
                try {
                    //изза ожидания skipped >100 frames
                    connected = mobTask.get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(),
                        String.valueOf(connected) ,Toast.LENGTH_SHORT).show();

                if(connected){
                    Intent intent2 = new Intent(getApplicationContext(), WorkActivity.class);
                    startActivity(intent2);
                }
            }
        });
    }
}
