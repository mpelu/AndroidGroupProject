package com.example.mpelu.androidgroupproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ocDatabase extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "Messages.db";
    private static int VERSION_NUM=5;

    public final static String TABLE_NAME;

    static {
        TABLE_NAME = "chat_message_tabele";
    }

    final static String KEY_ID = "_id";
    final static String KEY_MESSAGE= "MESSAGE";

    public ocDatabase(Context ctx){
        super(ctx,DATABASE_NAME,null,VERSION_NUM);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.i("ChatDatabaseHelper", "Calling onCreate");
        db.execSQL( "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, MESSAGE STRING);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("hatDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + " newVersion=" + newVersion);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void insert(ContentValues content){
    }
}
