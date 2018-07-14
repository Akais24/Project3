package com.example.q.swipe_tab;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class SearchUserActivity extends AppCompatActivity implements View.OnClickListener {
    TextView confirm;
    EditText input;
    RecyclerView search_rv;

    String server_url = "http://52.231.153.77:8080/search_by_name";
    Gson gson = new Gson();

    Search_Adapter mAdapter;
    ArrayList<User> debters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_user);

        confirm = findViewById(R.id.search_btn);
        input = findViewById(R.id.input);
        search_rv = findViewById(R.id.search_rv);

        confirm.setOnClickListener(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        search_rv.setLayoutManager(llm);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.search_btn:
                String word = String.valueOf(input.getText());

                String searchurl = server_url + "";

                JsonObject json = new JsonObject();
                json.addProperty("name", word);

                final ArrayList<search_result> saved = new ArrayList<>();

                Ion.with(getApplicationContext())
                        .load("POST", searchurl)
                        .setJsonObjectBody(json)
                        .asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray result) {
                                debters = new ArrayList<>();
                                Log.d("44444", String.valueOf(result));
                                if(result == null){
                                    Toast.makeText(getApplicationContext(), getString(R.string.server_die), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                for(int i=0; i<result.size(); i++){
                                    search_result newone = gson.fromJson(result.get(i), search_result.class);
                                    saved.add(newone);
                                    User newuser = new User(newone.unique_id, newone.name, newone.nickname);
                                    debters.add(newuser);
                                }
                                if(debters.size() == 0) debters.add(new User("","해당 유저 없음", ""));
                                mAdapter = new Search_Adapter(SearchUserActivity.this, debters, SearchUserActivity.this);
                                search_rv.setAdapter(mAdapter);
                            }
                        });

                break;
        }
    }

    public void selectItem(User target){
        Intent returnintent = new Intent();
        returnintent.putExtra("select", target);
        setResult(Activity.RESULT_OK, returnintent);
        finish();
    }

    class search_result{
        String unique_id;
        String name;
        String nickname;
    }
}
