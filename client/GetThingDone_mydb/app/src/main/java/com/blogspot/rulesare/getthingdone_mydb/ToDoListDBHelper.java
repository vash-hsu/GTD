package com.blogspot.rulesare.getthingdone_mydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ToDoListDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ToDoList.db";
    private static SQLiteDatabase database;

    public ToDoListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ToDoItemDAO.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // todo: to support upgrading historic data
        db.execSQL(ToDoItemDAO.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new ToDoListDBHelper(context).getWritableDatabase();
        }
        return database;
    }

}
