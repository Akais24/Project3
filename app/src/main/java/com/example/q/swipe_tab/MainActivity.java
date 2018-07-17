package com.example.q.swipe_tab;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.swipe_tab.AddEvent.AddActivity;
import com.example.q.swipe_tab.Random.Random_Camera_Activity;
import com.example.q.swipe_tab.Random.Random_Normal_Activity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int REQUEST_TAKE_PHOTO = 3000;
    final int SEND = 0;
    final int RECEIVE = 1;

    String name, nickname, unique_id;

    ImageView settings;
    ImageView send_more, receive_more;
    RecyclerView send_rv, receive_rv;
    TextView send_total, receive_total;
    SwipeRefreshLayout srl;

    ArrayList<Event> send_list, receive_list;
    Statistic_Adapter send_adapter, receive_adapter;

    ProgressDialog mProgressDialog;
    String server_url = "http://52.231.153.77:8080/";
    Gson gson = new Gson();

    private Animation fab_open, fab_close;
    private FloatingActionButton fab, fab_x, fab1, fab2, fab3;
    private LinearLayout fab1_ex, fab2_ex, fab3_ex;
    TextView cover;
    private Boolean isFabOpen = false;

    private String[] permissions;

    File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");
    String mCurrentPhotoPath = null;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send_more = findViewById(R.id.send_more_info);
        receive_more = findViewById(R.id.receive_more_info);
        send_rv = findViewById(R.id.send_statistic_rv);
        receive_rv = findViewById(R.id.receive_statistic_rv);
        send_total = findViewById(R.id.send_space);
        receive_total = findViewById(R.id.receive_space);
        srl = findViewById(R.id.swipe);
        settings = findViewById(R.id.settings);

        send_more.setOnClickListener(this);
        receive_more.setOnClickListener(this);
        settings.setOnClickListener(this);

        final SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
        name = test.getString("name", null);
        nickname = test.getString("nickname", null);
        unique_id = test.getString("unique_id", null);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("로딩 중입니다");

        LinearLayoutManager llm1 = new LinearLayoutManager(this);
        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        send_rv.setLayoutManager(llm1);
        receive_rv.setLayoutManager(llm2);
        srl.setOnRefreshListener(this);

        update_main();

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = findViewById(R.id.fab);
        fab_x = findViewById(R.id.fab_x);
        fab1 = findViewById(R.id.fab1);
        fab1_ex = findViewById(R.id.fab1_ex);
        fab2 = findViewById(R.id.fab2);
        fab2_ex = findViewById(R.id.fab2_ex);
        fab3 = findViewById(R.id.fab3);
        fab3_ex = findViewById(R.id.fab3_ex);
        cover = findViewById(R.id.cover);

        fab.setOnClickListener(this);
        cover.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        permissions = new String[3];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[1] =  Manifest.permission.CAMERA;
        permissions[2] = Manifest.permission.INTERNET;
    }

    public void update_main(){
        mProgressDialog.show();
        final int[] count = {0};
        send_list = new ArrayList<>();
        receive_list = new ArrayList<>();

        JsonObject json = new JsonObject();
        json.addProperty("unique_id", unique_id);

        Ion.with(getApplicationContext())
                .load("POST", server_url + "search_as_debtor")
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        Log.d("4444 debtor :", String.valueOf(result));
                        int total_send_amount = 0;
                        for(int i=0; i<result.size(); i++){
                            Event newevent = gson.fromJson(result.get(i), Event.class);
                            send_list.add(newevent);
                            total_send_amount += newevent.price;
                        }
                        count[0]++;
                        send_adapter = new Statistic_Adapter(getApplicationContext(), send_list);
                        send_rv.setAdapter(send_adapter);
                        send_total.setText(String.valueOf(total_send_amount) + "원");
                        if(count[0] == 2){
                            mProgressDialog.hide();
                        }
                    }
                });
        Ion.with(getApplicationContext())
                .load("POST", server_url + "search_as_creditor")
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        Log.d("4444 creditor :", String.valueOf(result));
                        int total_receive_amount = 0;
                        for(int i=0; i<result.size(); i++){
                            Event newevent = gson.fromJson(result.get(i), Event.class);
                            receive_list.add(newevent);
                            total_receive_amount  += newevent.price;
                        }
                        count[0]++;
                        receive_adapter = new Statistic_Adapter(getApplicationContext(), receive_list);
                        receive_rv.setAdapter(receive_adapter);
                        receive_total.setText(String.valueOf(total_receive_amount) + "원");
                        if(count[0] == 2){
                            mProgressDialog.hide();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        Intent more_info;
        Bundle args;
        switch (v.getId()) {
            case R.id.settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.send_more_info:
                more_info = new Intent(MainActivity.this, MoreInfoActivity.class);

                args = new Bundle();
                args.putSerializable("list", (Serializable) send_list);
                more_info.putExtra("BUNDLE", args);
                more_info.putExtra("category", SEND);

                startActivity(more_info);
                break;
            case R.id.receive_more_info:
                more_info = new Intent(MainActivity.this, MoreInfoActivity.class);

                args = new Bundle();
                args.putSerializable("list", (Serializable) receive_list);
                more_info.putExtra("BUNDLE", args);
                more_info.putExtra("category", RECEIVE);

                startActivity(more_info);
                break;
            case R.id.fab:
            case R.id.cover:
                anim();
                break;
            case R.id.fab_x:
                anim();
                break;
            case R.id.fab1:
                Intent add = new Intent(MainActivity.this, AddActivity.class);
                startActivity(add);
                anim();
                break;
            case R.id.fab2:
                anim();
                Intent normal_random = new Intent(MainActivity.this, Random_Normal_Activity.class);
                startActivity(normal_random);
                break;
            case R.id.fab3:
                anim();
                if(checkselfpermission(permissions)){
                    Toast.makeText(getApplicationContext(), "카메라와 파일 접근 권한이 없어 실행할 수 없습니다. 앱을 재실행하여 권한을 허가해주시길 바랍니다", Toast.LENGTH_LONG).show();
                }else{
                    captureCamera();
                }
                break;

        }
    }

    @Override
    public void onRefresh() {
        update_main();
        srl.setRefreshing(false);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class Event implements Serializable {
        String ID;
        String unique_id;
        String name;
        String nickname;
        Integer price;
        String date;
        String info;
    }

    public void anim() {
        if (isFabOpen) {
            fab.setVisibility(View.VISIBLE);
            fab_x.setVisibility(View.INVISIBLE);
            fab1_ex.startAnimation(fab_close);
            fab2_ex.startAnimation(fab_close);
            fab3_ex.startAnimation(fab_close);;
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            cover.setVisibility(View.INVISIBLE);
            isFabOpen = false;
        } else {
            fab.setVisibility(View.INVISIBLE);
            fab_x.setVisibility(View.VISIBLE);
            fab1_ex.startAnimation(fab_open);
            fab2_ex.startAnimation(fab_open);
            fab3_ex.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            cover.setVisibility(View.VISIBLE);
            isFabOpen = true;
        }
    }

    private boolean checkselfpermission(String[] permissions){
        for(int i=0; i<permissions.length; i++){
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }


    private void captureCamera() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                    imageUri = providerURI;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile;

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("REQUEST CODE : ", String.valueOf(requestCode));
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");

                        //do my work
                        Intent random = new Intent(MainActivity.this, Random_Camera_Activity.class);
                        random.putExtra("filepath", mCurrentPhotoPath);
                        random.putExtra("imageuri", imageUri.toString());

                        startActivity(random);

                        Log.d("CheckURI", String.valueOf(imageUri));
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                }
                break;
        }
    }
}
