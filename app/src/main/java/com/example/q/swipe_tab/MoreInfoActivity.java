package com.example.q.swipe_tab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.q.swipe_tab.AddEvent.User;

import java.util.ArrayList;

public class MoreInfoActivity extends AppCompatActivity {
    final int SEND = 0;
    final int RECEIVE = 1;

    int category;
    ArrayList<MainActivity.Event> target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        Intent intent = getIntent();
        category = intent.getIntExtra("category", SEND);
        Bundle args= intent.getBundleExtra("BUNDLE");
        target = (ArrayList<MainActivity.Event>) args.getSerializable("list");

        Log.d("4444 More info", String.valueOf(target));
    }
}
