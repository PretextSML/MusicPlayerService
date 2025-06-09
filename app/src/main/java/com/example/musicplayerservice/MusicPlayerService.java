package com.example.musicplayerservice;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class MusicPlayerService extends Service {
    private static final String TAG = "MusicPlayerService";

    private Context mContext;
    private final IBinder iBinder = new IMusicPlayerInterface.Stub() {
        @Override
        public void serviceTest() {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED)
                Log.d(TAG, "permission ok");
            else
                Log.d(TAG, "permission denied");
        }

        @Override
        public boolean checkcheckAuthentication(String username, String password) {
            return username.equals("test") && password.equals("test");
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mContext = getApplicationContext();
        return iBinder;
    }
}