package com.pretext.musicplayerservice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "[service]";


    void loadDatabase() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "start service......");
        startService(new Intent(getApplicationContext(), MusicPlayerService.class));
    }
}