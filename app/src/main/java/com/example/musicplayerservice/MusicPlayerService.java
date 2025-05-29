package com.example.musicplayerservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MusicPlayerService extends Service {
    private static final String TAG = "MusicPlayerService";

    private final IBinder iBinder = new IMusicPlayerInterface.Stub() {
        @Override
        public void serviceTest() {
            Log.d(TAG, "serviceTest");
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }
}