package com.example.q.swipe_tab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class MoreInfo_DetailActivity extends AppCompatActivity {
    final int SEND = 0;
    final int RECEIVE = 1;

    String unique_id;
    MainActivity.Event target;
    int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_detail);

        final SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
        unique_id = test.getString("unique_id", null);

        Intent from = getIntent();
        MainActivity.Event target = (MainActivity.Event) from.getSerializableExtra("target");
        category = from.getIntExtra("category", SEND);
    }
}
