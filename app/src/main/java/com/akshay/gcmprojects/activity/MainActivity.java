package com.akshay.gcmprojects.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.akshay.gcmprojects.R;
import com.akshay.gcmprojects.app.Config;
import com.akshay.gcmprojects.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMsg = (TextView) findViewById(R.id.txt_push_message);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    //gcm successfully registered.
                    //now subscribe to 'global' topic to receive app wide notifications.
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received.
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push Notification is : " + message, Toast.LENGTH_SHORT).show();
                    txtMsg.setText(message);
                }
            }
        };
        displayFirebaseRegId();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId",null);
        Log.e(TAG, "Firebase Reg Id: "+regId);

        if (!TextUtils.isEmpty(regId)){
            txtRegId.setText(String.format("Firebase reg id: %s", regId));
        } else {
            txtRegId.setText("Firebase Reg id is not received yet !..");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter(Config.REGISTRATION_COMPLETE));

        //register new push msg receiver
        //by doing this, the activity will be notified each time a new msg comes.

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter(Config.PUSH_NOTIFICATION));

        //clear the notification area when the app is opened.
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
