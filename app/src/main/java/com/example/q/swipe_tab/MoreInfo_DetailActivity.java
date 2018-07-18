package com.example.q.swipe_tab;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MoreInfo_DetailActivity extends AppCompatActivity implements View.OnClickListener {
    final int SEND = 0;
    final int RECEIVE = 1;

    RelativeLayout send_layout, receive_layout;
    TextView date, creditor, debtor, price, content, account;
    Button number, name_number, name_number_price, ask_again;

    MainActivity.Event target;
    String unique_id, account_info, name, nickname;
    String creditor_account = "";
    int category;

    Toast toast;

    String server_url = "http://52.231.153.77:8080/";
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_detail);

        final SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
        unique_id = test.getString("unique_id", null);
        account_info = test.getString("acount_info", null);
        name = test.getString("name", null);
        nickname = test.getString("nickname", null);

        Intent from = getIntent();
        target = (MainActivity.Event) from.getSerializableExtra("target");
        category = from.getIntExtra("category", SEND);

        send_layout = findViewById(R.id.send_layout);
        receive_layout = findViewById(R.id.receive_layout);

        date = findViewById(R.id.date_space);
        creditor = findViewById(R.id.creditor_space);
        debtor = findViewById(R.id.debtor_space);
        price = findViewById(R.id.price_space);
        content = findViewById(R.id.content_space);
        account = findViewById(R.id.account_space);

        number = findViewById(R.id.only_number);
        name_number = findViewById(R.id.name_number);
        name_number_price = findViewById(R.id.name_number_price);
        ask_again = findViewById(R.id.ask_again);

        date.setText(target.date);
        price.setText(String.valueOf(target.price) + "원");
        content.setText(target.info);

        if(category == SEND) send_work();
        else receive_work();

    }

    public void send_work(){
        send_layout.setVisibility(View.VISIBLE);
        receive_layout.setVisibility(View.GONE);

        JsonObject json = new JsonObject();
        json.addProperty("unique_id", target.unique_id);
        Log.d("44444 getaccount", target.unique_id);
        Log.d("44444 getaccount", server_url + "find_account_info");

        Ion.with(getApplicationContext())
                .load("POST", server_url + "find_account_info")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("44444 getaccout", String.valueOf(result));
                        account_response newone = gson.fromJson(result, account_response.class);
                        if(newone.result.equals("Success")){
                            creditor_account = newone.account_info;creditor.setText(target.name + "(" + target.nickname + ")");
                            debtor.setText(name + "(" + nickname + ")");
                            account.setText(creditor_account);

                        }else{
                            showtoast("계좌정보를 받아올 수 없습니다");
                        }
                    }
                });

        number.setOnClickListener(this);
        name_number.setOnClickListener(this);
        name_number_price.setOnClickListener(this);
    }

    public void receive_work(){
        send_layout.setVisibility(View.GONE);
        receive_layout.setVisibility(View.VISIBLE);

        debtor.setText(target.name + "(" + target.nickname + ")");
        creditor.setText(name + "(" + nickname + ")");
        account.setText(account_info);

        ask_again.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ClipData clipData;
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        switch (v.getId()){
            case R.id.ask_again:
                String isthistoken = FirebaseInstanceId.getInstance().getToken();
                Log.d("mytokeninclick", isthistoken);
                String test = "TEST";
                Log.d("8888", "Hellofromtheotherside");
                final SharedPreferences ForToken = getSharedPreferences("local", MODE_PRIVATE);
                JsonObject json = new JsonObject();
                json.addProperty("unique_id", ForToken.getString("unique_id", null));
                json.addProperty("ID", target.ID);
                Ion.with(getApplicationContext())
                        .load("POST", "http://52.231.153.77:8080/push")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                simple_response new2 = gson.fromJson(result, simple_response.class);
                                if (!new2.result.equals("Success")) {
                                    Toast.makeText(getApplicationContext(), "독촉에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    SharedPreferences.Editor editor = ForToken.edit();
                                    editor.putString("acount_info", account_info);
                                    editor.putString("name", name);
                                    editor.putString("nickname", nickname);
                                    editor.commit();
                                    Toast.makeText(getApplicationContext(), "독촉에 성공하였습니다", Toast.LENGTH_SHORT).show();
                                    Log.d("44444", "독촉성공");
                                    finish();
                                }
                            }
                        });
                return;
            case R.id.only_number:
                String[] words = creditor_account.split(" ");
                if(words.length == 1){
                    showtoast("계좌번호만 분리할 수 없는 형식의 계좌입니다");
                    return;
                }
                String raw = "";
                for(int i=1; i<words.length; i++) raw += words[i];
                clipData = ClipData.newPlainText("label", raw);
                clipboardManager.setPrimaryClip(clipData);
                showtoast("계좌번호를 클립보드에 복사하였습니다");
                break;
            case R.id.name_number:
                clipData = ClipData.newPlainText("label", creditor_account);
                clipboardManager.setPrimaryClip(clipData);
                showtoast("은행명 + 계좌번호를 클립보드에 복사하였습니다");
                break;
            case R.id.name_number_price:
                clipData = ClipData.newPlainText("label", creditor_account + " " + String.valueOf(target.price) + "원");
                clipboardManager.setPrimaryClip(clipData);
                showtoast("은행명 + 계좌번호 + 금액을 클립보드에 복사하였습니다");
                break;
        }
    }

    public void showtoast(String text){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
