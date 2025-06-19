package com.pretext.musicplayerservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MusicPlayerUserManager {
    public static final String TAG = "[UserManager]";
    private static MusicPlayerDBOpenHelper dbOpenHelper;
    private static MusicPlayerUserManager userManager;

    public static MusicPlayerUserManager getInstance() {
        if (userManager == null)
            userManager = new MusicPlayerUserManager();
        Log.d(TAG, "getInstance: " + userManager);
        return userManager;
    }

    public void setDbOpenHelper(Context context) {
        dbOpenHelper = new MusicPlayerDBOpenHelper(context);
        Log.d(TAG, "setDbOpenHelper: " + dbOpenHelper);
    }

    public void addUser(String user, String password, String history) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MusicPlayerDBOpenHelper.COLUMN_USER, user);
        values.put(MusicPlayerDBOpenHelper.COLUMN_PASSWORD, password);
        values.put(MusicPlayerDBOpenHelper.COLUMN_HISTORY, history);

        db.insert(MusicPlayerDBOpenHelper.TABLE_USERS, null, values);
        db.close();
    }

    public boolean authenticatedUser(String user, String password) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        boolean isCorrect = false;
        Cursor cursor = db.query(
                MusicPlayerDBOpenHelper.TABLE_USERS,
                new String[]{MusicPlayerDBOpenHelper.COLUMN_PASSWORD},
                MusicPlayerDBOpenHelper.COLUMN_USER + " = ?",
                new String[]{user},
                null, null, null);

        if (cursor.moveToFirst()) {
            Log.d(TAG, "authenticatedUser: " + cursor.getString(0));
            if (cursor.getString(0).equals(password)) {
                isCorrect = true;
            }
        }

        cursor.close();
        db.close();

        return isCorrect;
    }

    public boolean isExists(String user) {
        Log.d(TAG, "isExists: " + user);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(
                MusicPlayerDBOpenHelper.TABLE_USERS,
                new String[]{MusicPlayerDBOpenHelper.COLUMN_USER},
                MusicPlayerDBOpenHelper.COLUMN_USER + " = ?",
                new String[]{user},
                null, null, null);
        boolean isExists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return isExists;
    }

    public String getUserHistory(String user) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String history = null;
        Cursor cursor = db.query(
                MusicPlayerDBOpenHelper.TABLE_USERS,
                new String[]{MusicPlayerDBOpenHelper.COLUMN_HISTORY},
                MusicPlayerDBOpenHelper.COLUMN_USER + " = ?",
                new String[]{user},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            history = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return history;
    }

    public void updateHistory(String user, String history) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MusicPlayerDBOpenHelper.COLUMN_HISTORY, history);
        db.update(MusicPlayerDBOpenHelper.TABLE_USERS,
                values,
                MusicPlayerDBOpenHelper.COLUMN_USER + " = ?",
                new String[]{user}
        );
        db.close();
    }
}
