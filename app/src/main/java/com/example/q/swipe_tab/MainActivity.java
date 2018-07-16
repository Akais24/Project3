package com.example.q.swipe_tab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.q.swipe_tab.AddEvent.AddActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String nickname;
    String unique_id;
    String image_url;

    private FloatingActionButton fab_add;
    Button settings;
    ImageView send_more, receive_more;
    RecyclerView send_rv, receive_rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab_add = findViewById(R.id.fab_add);
        send_more = findViewById(R.id.send_more_info);
        receive_more = findViewById(R.id.receive_more_info);
        send_rv = findViewById(R.id.send_statistic_rv);
        receive_rv = findViewById(R.id.receive_statistic_rv);
        settings = findViewById(R.id.settings);

        settings.setOnClickListener(this);
        fab_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                //do add activity
                Intent add = new Intent(MainActivity.this, AddActivity.class);
                startActivity(add);
                break;
            case R.id.settings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
        }
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
}
