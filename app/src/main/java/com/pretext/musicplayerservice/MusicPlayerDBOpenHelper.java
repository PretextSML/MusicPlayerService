package com.pretext.musicplayerservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicPlayerDBOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_HISTORY = "history";
    private static final String DATABASE_NAME = "MusicPlayerUserDB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_USERS + "(" +
                    COLUMN_USER + " TEXT PRIMARY KEY," +
                    COLUMN_PASSWORD + " TEXT NOT NULL," +
                    COLUMN_HISTORY + " TEXT" + ")";

    public MusicPlayerDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}
