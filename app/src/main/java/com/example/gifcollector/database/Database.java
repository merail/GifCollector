package com.example.gifcollector.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import static com.example.gifcollector.database.DatabaseContract.Entry.COLUMN_NAME_SUBTITLE;
import static com.example.gifcollector.database.DatabaseContract.Entry.COLUMN_NAME_TITLE;
import static com.example.gifcollector.database.DatabaseContract.Entry.TABLE_NAME;

public class Database {
    private static Database sDatabase;
    private static SQLiteDatabase readDatabase;
    private static SQLiteDatabase writeDatabase;

    public final String[] projection = {
            BaseColumns._ID,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_SUBTITLE
    };
    public final String sortOrder = COLUMN_NAME_SUBTITLE + " DESC";

    private Database(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        readDatabase = helper.getWritableDatabase();
        writeDatabase = helper.getWritableDatabase();
    }

    public static Database get(Context context) {
        if (sDatabase == null)
            sDatabase = new Database(context);

        return sDatabase;
    }

    public String readData(String columnTitle) {
        Cursor cursor = readDatabase.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        String record = "";
        while (cursor.moveToNext()) {
            String title = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_NAME_TITLE));
            if (title.equals(columnTitle))
                record = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_NAME_SUBTITLE));
        }
        cursor.close();

        return record;
    }

    public void deleteData(String columnTitle) {
        readDatabase.delete(TABLE_NAME,
                COLUMN_NAME_TITLE + "='" + columnTitle + "'", null);
    }

    public void writeData(String columnTitle, String data) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME_TITLE, columnTitle);
        values.put(COLUMN_NAME_SUBTITLE, data);

        writeDatabase.insert(TABLE_NAME, null, values);
    }
}
