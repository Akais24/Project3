package com.example.q.swipe_tab;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onCreate() {
        Log.d(TAG, "Service Created");
        super.onCreate();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        final SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
        SharedPreferences.Editor editor = test.edit();
        editor.putString("token", refreshedToken);
        editor.commit();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }
}
