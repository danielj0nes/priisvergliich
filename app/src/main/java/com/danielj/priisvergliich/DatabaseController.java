package com.danielj.priisvergliich;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USER_TABLE";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    public DatabaseController(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + USER_TABLE
                        + " (ID INTEGER DEFAULT 0 PRIMARY KEY, "
                        + LATITUDE + " REAL, "
                        + LONGITUDE + " REAL)";
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public boolean modify(UserModel userModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Working with cv for convenience
        ContentValues cv = new ContentValues();
        cv.put(LATITUDE, userModel.getLatitude());
        cv.put(LONGITUDE, userModel.getLongitude());
        // First check whether or not database is empty
        long rowCheck = DatabaseUtils.queryNumEntries(db, USER_TABLE);
        if (rowCheck == 0) {
            final long insert = db.insert(USER_TABLE, null, cv);
            // False if fail
            return insert != -1;
        } else {
            // Otherwise update the entry in user table, since id is always 0, query for it
            final long update = db.update(USER_TABLE, cv, "id = ?", new String[]{"0"});
            return update != -1;
        }
    }
}
