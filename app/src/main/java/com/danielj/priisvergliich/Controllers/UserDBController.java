package com.danielj.priisvergliich.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.danielj.priisvergliich.Models.ProductModel;
import com.danielj.priisvergliich.Models.UserModel;

import java.io.ByteArrayOutputStream;
import java.util.List;

/*This controller is used to handle the primary CRUD database operations.*/
public class UserDBController extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USER_TABLE";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    public UserDBController(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createUsers = "CREATE TABLE IF NOT EXISTS " + USER_TABLE
                    + " (ID INTEGER DEFAULT 0 PRIMARY KEY, "
                    + LATITUDE + " REAL, "
                    + LONGITUDE + " REAL)";
            db.execSQL(createUsers);
        } catch (Exception e) {
            // This really shouldn't occur unless the SQL queries are fundamentally incorrect
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    /*The modifyUser function is used to either insert a completely new user with location data
    * into the model or to update existing location data. At the moment only location information is
    * related to the user in this app, this is likely to change and could be easily modified to fit.*/
    public boolean modifyUser(UserModel userModel) {
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
