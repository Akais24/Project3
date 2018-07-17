package com.example.q.swipe_tab.Random;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.q.swipe_tab.R;

public class Random_Camera_Activity extends AppCompatActivity {
    ImageView img;

    String filepath;
    Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_camera);

        Intent from = getIntent();
        filepath = from.getStringExtra("filepath");
        imageuri = Uri.parse(from.getStringExtra("imageuri"));

        img = findViewById(R.id.img_view);
//        File imgfile = new File(filepath);
//        if(imgfile.exists()){
//            Bitmap bm = BitmapFactory.decodeFile(imgfile.getAbsolutePath());
//            img.setImageBitmap(bm);
//        }
        img.setImageURI(imageuri);
    }
}
