package com.example.q.swipe_tab.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.q.swipe_tab.MainActivity;
import com.example.q.swipe_tab.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import com.kakao.util.helper.log.Logger;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class KakaoSignupActivity extends AppCompatActivity {
    String unique_id;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_signup);
        requestMe();
    }

    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }
            @Override
            public void onNotSignedUp() {} // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                final String kakaoID = String.valueOf(userProfile.getId()); // userProfile에서 ID값을 가져옴
                final String kakaoNickname = userProfile.getNickname();     // Nickname 값을 가져옴
                final String url = String.valueOf(userProfile.getProfileImagePath());

                Logger.d("UserProfile : " + userProfile);
                Log.d("kakao", "==========================");
                //Log.d("kakao", ""+userProfile);
                Log.d("kakao", kakaoID);
                Log.d("kakao", kakaoNickname);
                Log.d("kakao", "==========================");
                unique_id = kakaoID;
                //redirectMainActivity(); // 로그인 성공시 MainActivity로

                final SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
                SharedPreferences.Editor editor = test.edit();
                editor.putString("unique_id", kakaoID);
                editor.putString("nickname", kakaoNickname);
                editor.commit();

                JsonObject json = new JsonObject();
                json.addProperty("unique_id", kakaoID);

                Ion.with(getApplicationContext())
                        .load("POST", "http://52.231.153.77:8080/checkuser")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if(result == null){
                                    Toast.makeText(getApplicationContext(), getString(R.string.server_die), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Log.d("44444 checkuser", String.valueOf(result));
                                user_inform_response check = gson.fromJson(result, user_inform_response.class);
                                if(check.result.equals("Success")){
                                    SharedPreferences.Editor editor = test.edit();
                                    editor.putString("acount_info", check.account_info);
                                    editor.putString("name", check.name);
                                    editor.putString("nickname", check.nickname);
                                    String isthistoken = FirebaseInstanceId.getInstance().getToken();
                                    editor.putString("token", isthistoken);
                                    editor.commit();

                                    JsonObject fortoken = new JsonObject();
                                    SharedPreferences inform = getSharedPreferences("local", MODE_PRIVATE);
                                    String uid = inform.getString("unique_id", null);
                                    fortoken.addProperty("unique_id", uid);
                                    fortoken.addProperty("token", isthistoken);
                                    Log.d("4444", "send token : " + isthistoken);

                                    Ion.with(getApplicationContext())
                                            .load("Post", "http://52.231.153.77:8080/changeToken")
                                            .setJsonObjectBody(fortoken)
                                            .asJsonObject()
                                            .setCallback(new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onCompleted(Exception e, JsonObject result) {
                                                    Log.d("44444", "token change result : " + String.valueOf(result));
                                                }
                                            });
                                    redirectMainActivity();
                                }
                                if(check.result.equals("Failure")){
                                    Intent signup = new Intent(KakaoSignupActivity.this, SignUpActivity.class);
                                    signup.putExtra("id", kakaoID);
                                    startActivity(signup);
                                    finish();
                                }
                            }
                        });

            }
        });
    }
    private void redirectMainActivity() {
        Intent intent = new Intent(KakaoSignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
