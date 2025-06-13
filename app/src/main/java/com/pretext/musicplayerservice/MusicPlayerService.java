package com.pretext.musicplayerservice;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerService extends Service {
    private static final String TAG = "[service]";

    private MediaPlayer mediaPlayer;
    private final IBinder iBinder = new IMusicPlayerInterface.Stub() {

        @Override
        public void playMusic(String musicPath) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(musicPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException | RuntimeException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public List<MusicInfo> readMusicFile() {
            List<MusicInfo> musicInfoList = new ArrayList<>();
            Cursor cursor = getApplicationContext().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    musicInfoList.add(
                            new MusicInfo(
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                            )
                    );
                }
                cursor.close();
            } else {
                Log.d(TAG, "cursor is null!");
            }

            return musicInfoList;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }
}