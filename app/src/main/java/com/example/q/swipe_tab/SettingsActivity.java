package com.example.q.swipe_tab;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.q.swipe_tab.Login.LoginActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    EditText change_uinfo_name;
    EditText change_uinfo_nickname;
    EditText change_uinfo_account_info;
    Button submit_uinfo, backtomain, testbutton;
    String name, nickname, account_info, searchurl, uid;
    Gson gson = new Gson();
    SharedPreferences info;

    CardView logout;

    public static Activity myself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myself = SettingsActivity.this;

        info = getSharedPreferences("local", MODE_PRIVATE);
        name = info.getString("name", null);
        nickname = info.getString("nickname", null);
        account_info = info.getString("acount_info", null);
        uid = info.getString("unique_id", null);

        change_uinfo_name = findViewById(R.id.change_uinfo_name);
        change_uinfo_nickname = findViewById(R.id.change_uinfo_nickname);
        change_uinfo_account_info = findViewById(R.id.change_uinfo_account_info);
        submit_uinfo = findViewById(R.id.submit_uinfo);
        logout = findViewById(R.id.logout_btn);

        testbutton = findViewById(R.id.button);
        change_uinfo_name.setText(name);

        testbutton.setOnClickListener(this);
        backtomain = findViewById(R.id.backto_main);
        backtomain.setOnClickListener(this);
        change_uinfo_nickname.setText(nickname);
        change_uinfo_account_info.setText(account_info);
        change_uinfo_name.setOnClickListener(this);
        change_uinfo_nickname.setOnClickListener(this);
        change_uinfo_account_info.setOnClickListener(this);
        submit_uinfo.setOnClickListener(this);
        logout.setOnClickListener(this);
        searchurl = "http://52.231.153.77:8080/update_user";
    }

        public void onClick(View v) {
            JsonObject json = new JsonObject();
            switch(v.getId()){
                case R.id.button:
                    String isthistoken = FirebaseInstanceId.getInstance().getToken();
                    Log.d("mytokeninclick", isthistoken);
                    String test = "TEST";
                    Log.d("8888", "Hellofromtheotherside");
                    json.addProperty("unique_id", "812747582");
                    json.addProperty("ID", 14);
                    Ion.with(getApplicationContext())
                            .load("POST", "http://52.231.153.77:8080/push")
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    simple_response new2 = gson.fromJson(result, simple_response.class);
                                    if (!new2.result.equals("Success")) {
                                        Toast.makeText(getApplicationContext(), "회원 정보 변경에 실패하였습니다inbutton.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        SharedPreferences.Editor editor = info.edit();
                                        editor.putString("acount_info", account_info);
                                        editor.putString("name", name);
                                        editor.putString("nickname", nickname);
                                        editor.commit();
                                        Log.d("44444", "회원 정보 변경에 성공하였습니다inbutton.");
                                        finish();
                                    }
                                }
                            });
                    return;
                case R.id.backto_main:
                    finish();
                    break;
                case R.id.submit_uinfo:
                    if (change_uinfo_account_info.getText().length()>0){ account_info = change_uinfo_account_info.getText().toString();}
                    if (change_uinfo_name.getText().length()>0){ nickname = change_uinfo_nickname.getText().toString();}
                    if (change_uinfo_nickname.getText().length()>0){ name = change_uinfo_name.getText().toString();}
                    json.addProperty("name", name);
                    json.addProperty("nickname", nickname);
                    json.addProperty("account_info", account_info);
                    json.addProperty("unique_id", uid);

                    Log.d("8888", "done through this");
                    Ion.with(getApplicationContext())
                            .load("POST", searchurl)
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    Log.d("88888", " INSIDE YOUOHOH");
                                    simple_response newone = gson.fromJson(result, simple_response.class);
                                    Log.d("87878", "HEHEHEHeHE");
                                    if (result == null || !newone.result.equals("Success")) {
                                        Toast.makeText(getApplicationContext(), "회원 정보 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        SharedPreferences.Editor editor = info.edit();
                                        editor.putString("acount_info", account_info);
                                        editor.putString("name", name);
                                        editor.putString("nickname", nickname);
                                        editor.commit();
                                        Log.d("44444", "회원 정보 변경에 성공하였습니다.");
                                        finish();
                                    }
                                }
                            });
                    break;
                case R.id.logout_btn:
                    requestLogout();
                    break;
            }
        }

    protected void requestLogout() { //유저의 정보를 받아오는 함수
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                SharedPreferences.Editor edit = info.edit();
                edit.clear();
                edit.commit();
                ActivityCompat.finishAffinity(myself);
                Intent login = new Intent(myself, LoginActivity.class);
                startActivity(login);
            }
        });
    }
}
