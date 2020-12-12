package com.example.wallsdetector.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.wallsdetector.ClientSocket.MobSocket;
import com.example.wallsdetector.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class FragmentPicture extends Fragment {

    private ImageView iv_image;
    private Button b_send_photo;
    private Button b_upload_photo;

    private MobSocket mobSocket;
    private ControllerTask controllerTask;

    private boolean sended = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_picture, container, false);
        mobSocket = MobSocket.getInstance();
        controllerTask = new ControllerTask();

        b_upload_photo = v.findViewById(R.id.b_picture_upload_photo);
        b_send_photo = v.findViewById(R.id.b_picture_send_photo);
        iv_image = v.findViewById(R.id.iv_picture);

        b_send_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sended){
                    sended = false;
                    if(iv_image.getImageMatrix() != null){
                        Bitmap bitmap = ((BitmapDrawable) iv_image.getDrawable()).getBitmap();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageInByte = baos.toByteArray();
                        try {
                            baos.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(v.getContext(),"sending picture.." ,Toast.LENGTH_SHORT).show();

                        new ControllerTask().execute(imageInByte);
                    }
                    else {
                        Toast.makeText(v.getContext(),"no one picture chosen!" ,Toast.LENGTH_SHORT).show();
                        sended = true;
                    }
                }
            }
        });

        b_upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(getContext());
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity();
        if(resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        iv_image.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri returnUri = data.getData();
                        Bitmap bitmapImage = null;
                        try {
                            bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        iv_image.setImageBitmap(bitmapImage);
                    }
                    break;
            }
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    class ControllerTask extends AsyncTask<byte[], Void, Void> {

        String server_ms = "-_-";
        byte[] im_bytes;

        @Override
        protected Void doInBackground(byte[] ... params) {
            try {
                mobSocket.sendMessage(params[0]);
                Log.d("info", "send message: " + params[0].length);
                // принимаем обработанное фото
                im_bytes = mobSocket.getMessage();
                Log.d("info", "get message: " + im_bytes.length);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("info", "task ended");
            Toast.makeText(getContext(),server_ms,Toast.LENGTH_SHORT).show();

            // отображаем обработанное изображение
            Bitmap bitmap = BitmapFactory.decodeByteArray(im_bytes, 0, im_bytes.length);
            iv_image.setImageBitmap(bitmap);

            sended = true;
        }
    }
}
