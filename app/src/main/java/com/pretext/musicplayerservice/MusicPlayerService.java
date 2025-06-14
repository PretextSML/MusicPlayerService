package com.pretext.musicplayerservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayerService extends Service {
    private static final String TAG = "[service]";
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private IMusicProgressCallback musicProgressCallback;
    private Boolean isPause = false;
    private Timer timer;

    private final IBinder iBinder = new IMusicPlayerInterface.Stub() {

        @Override
        public void stopTimer() {
            timer.cancel();
        }

        @Override
        public void playMusic(String musicPath) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    try {
                        musicProgressCallback.onProgressChanged(mediaPlayer.getCurrentPosition());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void pauseMusic() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPause = true;
                }
            }
        }

        @Override
        public void resumeMusic() {
            if (isPause) {
                isPause = false;
                mediaPlayer.start();
            }
        }

        @Override
        public void stopMusic() {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }
        }

        @Override
        public void registerCallback(IMusicProgressCallback callback) {
            Log.d(TAG, "registerCallback: " + callback);
            musicProgressCallback = callback;
        }

        @Override
        public void unregisterCallback(IMusicProgressCallback callback) {
            musicProgressCallback = null;
        }

        @Override
        public void sendData() {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (mediaPlayer != null) {
                            musicProgressCallback.onProgressChanged(mediaPlayer.getCurrentPosition());
                        } else {
                            musicProgressCallback.onProgressChanged(0);
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 0, 100);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }
}