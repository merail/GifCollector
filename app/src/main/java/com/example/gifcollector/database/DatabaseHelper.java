package com.example.gifcollector.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DatabaseReader.db";

    public final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.Entry.TABLE_NAME + " (" +
                    DatabaseContract.Entry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.Entry.COLUMN_NAME_TITLE + " TEXT," +
                    DatabaseContract.Entry.COLUMN_NAME_SUBTITLE + " TEXT)";
    public final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.Entry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
