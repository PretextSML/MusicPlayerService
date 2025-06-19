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
            Log.d(TAG, "cancel timer");
            timer.cancel();
        }


        @Override
        public void playMusic(String musicPath) {
            Log.d(TAG, "play Music");
            try {
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    Log.d(TAG, "playMusic: complete");
                    try {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.setOnCompletionListener(null);
                            mediaPlayer.reset();
                            musicProgressCallback.onFinishPlaying();
                        }
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setDuration(int duration) {
            mediaPlayer.seekTo(duration);
            mediaPlayer.start();
        }

        @Override
        public void pauseMusic() {
            Log.d(TAG, "pause Music: isplaying = " + mediaPlayer.isPlaying());
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPause = true;
            }
        }

        @Override
        public void resumeMusic() {
            Log.d(TAG, "resumeMusic: ispause = " + isPause);
            if (isPause) {
                isPause = false;
                mediaPlayer.start();
            }
        }

        @Override
        public void stopMusic() {
            Log.d(TAG, "stopMusic");
            mediaPlayer.reset();
        }

        @Override
        public void registerCallback(IMusicProgressCallback callback) {
            Log.d(TAG, "registerCallback: " + callback);
            musicProgressCallback = callback;
        }

        @Override
        public void unregisterCallback(IMusicProgressCallback callback) {
            Log.d(TAG, "unregisterCallback: " + callback);
            musicProgressCallback = null;
        }

        @Override
        public void setHistory(String user, String history) {
            MusicPlayerUserManager.getInstance().updateHistory(user, history);
        }

        @Override
        public void setDBHelper() {
            MusicPlayerUserManager.getInstance().setDbOpenHelper(getApplicationContext());
        }

        @Override
        public boolean addNewUser(String user, String password) {
            if (MusicPlayerUserManager.getInstance().isExists(user)) {
                return true;
            }
            MusicPlayerUserManager.getInstance().addUser(user, password, "");
            return false;
        }

        @Override
        public boolean authenticatedUser(String user, String password) {
            Log.d(TAG, "authenticatedUser: " + user + ", " + password);
            if (!MusicPlayerUserManager.getInstance().isExists(user))
                return false;

            return MusicPlayerUserManager.getInstance().authenticatedUser(user, password);
        }

        @Override
        public String getHistory(String user) {
            return MusicPlayerUserManager.getInstance().getUserHistory(user);
        }

        @Override
        public void startTimer() {
            Log.d(TAG, "startTimer");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        musicProgressCallback.onProgressChanged(mediaPlayer.getCurrentPosition());
                        musicProgressCallback.onPlayStatusChanged(mediaPlayer.isPlaying());
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