package com.akshay.gcmprojects.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.akshay.gcmprojects.app.Config;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by akshaythalakoti on 11/6/16.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Saving Reg ID to sharedPrefs
        storeRegIdInPref(refreshedToken);

        //sending Reg ID to Server
        sendRegistrationToServer(refreshedToken);

        //Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token",refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        //sending gcm token to server.
        Log.e(TAG, "sendRegistrationToServer: "+    refreshedToken);
    }

    private void storeRegIdInPref(String refreshedToken) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF,0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId",refreshedToken);
        editor.commit();
    }
}
