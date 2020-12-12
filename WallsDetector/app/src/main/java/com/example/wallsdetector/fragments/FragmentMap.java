//package com.example.wallsdetector.fragments;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.ColorDrawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.fragment.app.Fragment;
//
//import com.example.wallsdetector.ClientSocket.MobSocket;
//import com.example.wallsdetector.R;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class FragmentMap extends Fragment {
//
//    View v;
//    CarSocket carSocket;
//
//    Button b_size_ok;
//    EditText et_map_x;
//    EditText et_map_y;
//
//    Button b_create;
//    Button b_send;
//    Button b_upload;
//    LinearLayout ll_map_size;
//    LinearLayout ll_map_square;
//
//    ArrayList<ArrayList<TextView>> map = new ArrayList<>();
//    int x;
//    int y;
//
//    private int Pick_image = 1;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        v = inflater.inflate(R.layout.fragment_map, container, false);
//
//        carSocket = CarSocket.getInstance();
//
//        b_size_ok = v.findViewById(R.id.b_size_ok);
//        et_map_x = v.findViewById(R.id.te_size_x);
//        et_map_y = v.findViewById(R.id.te_size_y);
//
//        b_create = v.findViewById(R.id.b_map_create);
//        b_upload = v.findViewById(R.id.b_map_upload);
//        b_send = v.findViewById(R.id.b_map_send);
//
//        ll_map_size = v.findViewById(R.id.ll_map_size);
//        //основной лайаут по размещению карты
//        ll_map_square = v.findViewById(R.id.ll_map_square);
//
//        b_upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                File file = new File(Environment.
//                        getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"a.bmp");
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                Log.d("log", String.format("bitmap size = %sx%s, byteCount = %s",
//                        bitmap.getWidth(), bitmap.getHeight(),
//                        (int) (bitmap.getByteCount() / 1024)));
//
//
////                ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
////                byte[] bytes = stream.toByteArray();
//
//
////
////                //Получаем URI изображения, преобразуем его в Bitmap
////                //объект и отображаем в элементе ImageView нашего интерфейса:
////                final Uri imageUri = intent.getData();
////                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
////                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
////
////                Toast.makeText(getContext(), "map uploaded" ,Toast.LENGTH_SHORT).show();
////
////                //переводим в массив байт
//////                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//////                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
//////                    byte[] bytes = stream.toByteArray();
//
//
//            }
//
//        });
//
//        b_create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ll_map_size.setVisibility(View.VISIBLE);
//            }
//        });
//
//        b_size_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                x = Integer.parseInt(et_map_x.getText().toString());
//                y = Integer.parseInt(et_map_y.getText().toString());
//
//                map = CreateMap(x, y);
//
//                ll_map_size.setVisibility(View.GONE);
//
//            }
//        });
//
//
//        b_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //переводим в стандартный вид и отправляем
//                double[][] d = TranslateToArray(map, x, y);
//
//                //сделать метод расширяющий границы массива
//                String[] str = MapEditor.TranslateArrayToPath(d);
//
//                if(str != null){
//                    Toast.makeText(getContext(), Arrays.toString(str),Toast.LENGTH_LONG).show();
//                    new MapTask().execute(Arrays.toString(str));
//                }
//                else {
//                    Toast.makeText(getContext(), "can't translate map" ,Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//        return v;
//
//    }
//
//    private double[][] TranslateToArray(ArrayList<ArrayList<TextView>> map, int x, int y){
//
//        double[][] map_d = new double[x][y];
//
//        for(int i = 0; i< x; i++){
//            for(int j = 0; j< y; j++){
//
//                if (map.get(i).get(j).getBackground() instanceof ColorDrawable) {
//                    ColorDrawable cd = (ColorDrawable) map.get(i).get(j).getBackground();
//                    int colorCode = cd.getColor();
//
//                    //0xFFFFFFFF - белый
//                    if(colorCode == 0xFFFFFFFF){
//                        map_d[i][j] = 0;
//                    }
//                    else if(colorCode == 0xFF000000){
//                        map_d[i][j] = 1;
//                    }
//                    else if(colorCode == 0xFF005400){
//                        map_d[i][j] = 0.1;
//                    }
//                    else if(colorCode == 0xFF9B3C2E){
//                        map_d[i][j] = 0.5;
//                    }
//
//                    //очищение
//                    map.get(i).get(j).setBackgroundColor(0xFFFFFFFF);
//
//                }
//
//            }
//        }
//
//        //изменяем базовую кнопку
//        map.get(x-2).get((int)(y/2)-1).setBackgroundColor(0xFF005400);
//
//
//        for (double[] doubles : map_d) {
//            System.out.println(Arrays.toString(doubles));
//        }
//
//        return map_d;
//
//    }
//
//    private ArrayList<ArrayList<TextView>> CreateMap(int ii, int jj){
//
//        ArrayList<ArrayList<TextView>> map = new ArrayList<>();
//
//        ll_map_square = v.findViewById(R.id.ll_map_square);
//
//        for(int i = 0; i< ii; i++){
//
//            ArrayList<TextView> mm = new ArrayList<>();
//
//            LinearLayout ll_map_line = new LinearLayout(getContext());
//            ll_map_line.setOrientation(LinearLayout.HORIZONTAL);
//            ll_map_line.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT));
//
//            for(int j = 0; j< jj; j++){
//
//                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                lParams.setMargins(1,1,1,1);
//
//                TextView tv = new TextView(getContext());
//                //0xFFFFFFFF - белый
//                tv.setBackgroundColor(0xFFFFFFFF);
//                tv.setHeight(80- (ii*2));
//                tv.setWidth(80- (ii*2));
//
//                tv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        v.setBackgroundColor(0xFF000000);
//
//                    }
//                });
//                tv.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        v.setBackgroundColor(0xFF9B3C2E);
//                        return true;
//                    }
//                });
//
//                ll_map_line.addView(tv, lParams);
//
//                mm.add(tv);
//
//            }
//
//            ll_map_square.addView(ll_map_line);
//
//            map.add(mm);
//
//        }
//
//        //изменяем базовую кнопку
//        map.get(ii-2).get((int)(jj/2)-1).setBackgroundColor(0xFF005400);
//        map.get(ii-2).get((int)(jj/2)-1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        map.get(ii-2).get((int)(jj/2)-1).setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return false;
//            }
//        });
//
//
//        return map;
//
//    }
//
//
//    class MapTask extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String ... params) {
//            try {
//                carSocket.sendMessage(params[0]);
//                Log.d("info", "send " + params[0]);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Log.d("info", "task ended");
//            Toast.makeText(getContext(), "path sanded" ,Toast.LENGTH_SHORT).show();
////            sended = true;
//        }
//    }
//
//
//
//
//}
//
