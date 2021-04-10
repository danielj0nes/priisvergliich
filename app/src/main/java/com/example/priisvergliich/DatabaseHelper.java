package com.example.priisvergliich;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String USER_TABLE = "USER_TABLE";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + USER_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + LATITUDE + " REAL, " + LONGITUDE + " REAL)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insert() {
        SQLiteDatabase db = this.getWritableDatabase();
        
    }
}
