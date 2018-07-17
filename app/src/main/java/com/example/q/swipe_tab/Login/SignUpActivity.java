package com.example.q.swipe_tab.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.q.swipe_tab.MainActivity;
import com.example.q.swipe_tab.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText realname_text;
    EditText nickname_text;
    EditText acountname_text;
    Button next;

    String unique_id;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        unique_id = intent.getStringExtra("id");

        realname_text = findViewById(R.id.realname_space);
        nickname_text = findViewById(R.id.nickname_space);
        acountname_text = findViewById(R.id.account_space);
        next = findViewById(R.id.next);

        next.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next:
                if(realname_text.getText().length() <= 0 || nickname_text.getText().length() <= 0 || acountname_text.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(), "입력하지 않은 정보가 있습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String realname = realname_text.getText().toString();
                final String nickname = nickname_text.getText().toString();
                final String account = acountname_text.getText().toString();

                JsonObject json = new JsonObject();
                json.addProperty("unique_id", unique_id);
                json.addProperty("name", realname);
                json.addProperty("nickname", nickname);
                json.addProperty("account_info", account);


                //add token to customer info.
                String isthistoken = FirebaseInstanceId.getInstance().getToken();
                Log.d("isthistoken", isthistoken);
                final SharedPreferences ForToken = getSharedPreferences("local", MODE_PRIVATE);
                String token =  " FAILED GETTING ANY TOKEN";
                String a = ForToken.getString("token", token);

                Log.d("tokentokentoken", a);

                json.addProperty("token",a);


                Ion.with(getApplicationContext())
                        .load("POST", "http://52.231.153.77:8080/make_new_user")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                simple_response newone = gson.fromJson(result, simple_response.class);

                                if(!newone.result.equals("Success")){
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다. 닉네임을 바꿔서 시도해주십시오", Toast.LENGTH_SHORT).show();
                                    return;
                                }else{
                                    Log.d("44444", "Signup success");

                                    final SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = test.edit();
                                    editor.putString("acount_info", account);
                                    editor.putString("name", realname);
                                    editor.putString("nickname", nickname);
                                    editor.commit();
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                break;
        }
    }
}
