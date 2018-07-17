package com.example.q.swipe_tab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.q.swipe_tab.AddEvent.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class MoreInfoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    final int SEND = 0;
    final int RECEIVE = 1;

    String unique_id;

    SwipeRefreshLayout srl;
    RecyclerView target_rv;

    int category;

    ArrayList<MainActivity.Event> targets;
    MoreInfo_Adapter target_adapter;

    ProgressDialog mProgressDialog;
    String server_url = "http://52.231.153.77:8080/";
    Gson gson = new Gson();

    Activity myself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        myself = MoreInfoActivity.this;

        Intent intent = getIntent();
        category = intent.getIntExtra("category", SEND);
        Bundle args= intent.getBundleExtra("BUNDLE");
        targets = (ArrayList<MainActivity.Event>) args.getSerializable("list");

        Log.d("4444 More info", String.valueOf(targets));

        srl = findViewById(R.id.moreinfo_srl);
        target_rv = findViewById(R.id.moreinfo_rv);

        srl.setOnRefreshListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        target_rv.setLayoutManager(llm);

        target_adapter = new MoreInfo_Adapter(getApplicationContext(), targets, category, myself);
        target_rv.setAdapter(target_adapter);
        target_rv.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("로딩 중입니다");

        final SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
        unique_id = test.getString("unique_id", null);
    }

    @Override
    public void onRefresh() {
        update();
        srl.setRefreshing(false);
    }

    public void update(){
        mProgressDialog.show();

        targets = new ArrayList<>();

        JsonObject json = new JsonObject();
        json.addProperty("unique_id", unique_id);

        String target_url = server_url + "search_as_";

        if(category == SEND) target_url += "debtor";
        else target_url += "creditor";

        Log.d("4444 Server : ", target_url);
        Ion.with(getApplicationContext())
                .load("POST", target_url)
                .setJsonObjectBody(json)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        for(int i=0; i<result.size(); i++){
                            MainActivity.Event newevent = gson.fromJson(result.get(i), MainActivity.Event.class);
                            targets.add(newevent);
                        }
                        target_adapter = new MoreInfo_Adapter(getApplicationContext(), targets, category, myself);
                        target_rv.setAdapter(target_adapter);
                        mProgressDialog.hide();
                    }
                });
    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.recycler_horizontal_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
