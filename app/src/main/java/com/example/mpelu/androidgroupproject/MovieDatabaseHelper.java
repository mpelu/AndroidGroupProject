package com.example.mpelu.androidgroupproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author mpelu
 * @version 1.0
 * DatabaseHelper class
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {
    //Database variables
    static final int VERSION_NUM = 4;
    static final String DATABASE_NAME = "FavouriteMovies";
    static final String TABLE_NAME = "Movies";

    public MovieDatabaseHelper(Context ctx){
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * Creates database with specified name and columns
     * @param db - database being executed
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, Title text, Year INTEGER, Rated text, Runtime INTEGER, Actors text, Plot text)");
    }

    /**
     * Upgrades database
     * @param db - database being upgraded
     * @param oldVer - old database version
     * @param newVer - new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
