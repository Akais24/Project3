package com.example.q.swipe_tab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.util.Xml;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    final int MAIN_POPUP = 4500;

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Fromfirebase: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Messagefirebase data payload: " + remoteMessage.getData());
            //Toast.makeText(getApplicationContext(), serializedMap, Toast.LENGTH_SHORT).show();

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.

            } else {
                // Handle message within 10 seconds

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();

                    String body = "{\"name\" : \"재영\", \"nickname\" : \"altair\", \"info\": \"마루\", \"price\" : 8000, \"date\" : \"11/21\", \"account\": \"우리 1002\"}";
                    JsonElement jsonElement = new JsonParser().parse(body);
                    JsonObject target = jsonElement.getAsJsonObject();

                    String name = target.get("name").toString().split("\"")[1];
                    String nickname = target.get("nickname").toString().split("\"")[1];
                    String info = target.get("info").toString().split("\"")[1];
                    String price = target.get("price").toString();
                    String date = target.get("nickname").toString().split("\"")[1];
                    String account_info = target.get("info").toString().split("\"")[1];

                    Log.d("5555", name);
                    Log.d("5555", nickname);
                    Log.d("5555", info);
                    Log.d("5555", price);

                    Context mContext =getApplicationContext();

                    Intent main_popup = new Intent(mContext, MainActivity.class);
                    main_popup.putExtra("is_popup", true);
                    main_popup.putExtra("creditor_name", name);
                    main_popup.putExtra("creditor_nickname", nickname);
                    main_popup.putExtra("creditor_info", info);
                    main_popup.putExtra("creditor_price", price);
                    main_popup.putExtra("creditor_date", date);
                    main_popup.putExtra("creditor_account_info", account_info);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, MAIN_POPUP, main_popup, 0);

                    String title = String.format("%s(%s) 님께서 입금을 요청했습니다", name, nickname);
                    String bigcontent = String.format("%s 건에 대해서 %s원을 요청했습니다", info, price);

                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                    bigText.bigText(bigcontent);
                    bigText.setBigContentTitle(title);
                    bigText.setSummaryText("독촉메시지");

                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setSmallIcon(R.drawable.cookie);
                    mBuilder.setContentTitle(title);
                    mBuilder.setContentText(bigcontent);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setStyle(bigText);

                    NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("notify_001", getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    mNotificationManager.notify(1234, mBuilder.build());
                }
            });

            Log.d(TAG, "Messagefirebase Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
