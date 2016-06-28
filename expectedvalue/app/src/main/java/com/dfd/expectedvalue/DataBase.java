package com.dfd.expectedvalue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by a61-201405-2055 on 16/06/27.
 */
public class DataBase extends SQLiteOpenHelper {

    static final String DB = "calculate_application.db";
    static final int DB_VERSION = 1;

    public DataBase(Context context) {
        //データベース作成
        super(context, DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //テーブル作成
        db.execSQL("CREATE TABLE IF NOT EXISTS name_slot (id INTEGER PRIMARY KEY, name STRING, hatsu INTEGER, heikin INTEGER, tenjou INTEGER, tenjouonkei INTEGER, gamek INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
