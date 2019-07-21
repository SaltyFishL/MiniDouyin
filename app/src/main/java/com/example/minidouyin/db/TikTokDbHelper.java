package com.example.minidouyin.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author: jq_lu
 * @Date: 2019/7/21
 * @Time: 20:59
 */
public class TikTokDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tiktok.db";
    private static final int DB_VERSION = 1;

    public TikTokDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TikTokContract.SQL_CREATE_LIKE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
