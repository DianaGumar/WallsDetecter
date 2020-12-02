package com.example.wallsdetector.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.wallsdetector.ClientSocket.MobSocket;
import com.example.wallsdetector.R;

public class FragmentPicture extends Fragment {

    private Button b_send;
    private TextView tv_server_ansver;
    private EditText et_client_message;

    private MobSocket mobSocket;
    private ControllerTask controllerTask;

    private boolean sended = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_picture, container, false);

        mobSocket = MobSocket.getInstance();
        controllerTask = new ControllerTask();

        b_send = v.findViewById(R.id.b_picture_send);

        tv_server_ansver = v.findViewById(R.id.tv_picture_server_ansver);
        et_client_message = v.findViewById(R.id.et_picture_client_message);

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sended){
                    sended = false;
                    if(!et_client_message.getText().toString().equals("")){
                        new ControllerTask().execute(et_client_message.getText().toString(), "");
                    }
                    else {
                        Toast.makeText(v.getContext(),"fill message field" ,Toast.LENGTH_SHORT).show();
                        sended = true;
                    }
                }
            }
        });

        return v;
    }

    class ControllerTask extends AsyncTask<String, Void, Void> {

        String server_ms = "";

        @Override
        protected Void doInBackground(String ... params) {
            try {
                mobSocket.sendMessage(params[0].concat(params[1]));
                Log.d("info", "send ".concat(params[0]));

                server_ms = mobSocket.getMessage();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("info", "task ended");
            sended = true;
            tv_server_ansver.setText(server_ms);
        }
    }


}
