package com.blogspot.rulesare.getthingdone_mydb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ToDoItemDAO {
    protected static final String TABLE_NAME = "TaskStore";
    protected static final String KEY_ID = "_id"; // is it default style/practice?
    protected static final String COLUMN_NAME = "name";
    protected static final String COLUMN_ISCOMPLETE = "isComplete";
    protected static final String TEXT_TYPE = " TEXT";
    protected static final String INTEGER_TYPE = " INTEGER";
    protected static final String COMMA_SEP = ", ";

    /*
    CREATE TABLE TaskStore
    (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    isComplete INTEGER NOT NULL
    ); */
    protected static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_ISCOMPLETE + INTEGER_TYPE + " )";
    protected static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase db;

    public ToDoItemDAO(Context context) {
        db = ToDoListDBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    // insert item(name and isComplete),
    // return item(id)
    public ToDoItem insert(ToDoItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, item.getName());
        cv.put(COLUMN_ISCOMPLETE, item.getIsComplete());
        long id = db.insert(TABLE_NAME, null, cv);
        item.setId(id);
        return item;
    }

    public boolean update(ToDoItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, item.getName());
        cv.put(COLUMN_ISCOMPLETE, item.getIsComplete());
        String where = KEY_ID + "=" + String.valueOf(item.getId());
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public boolean delete(long id) {
        String where = KEY_ID + "=" + String.valueOf(id);
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    // get without id
    public List<ToDoItem> getAll() {
        List<ToDoItem> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }

    // get with id
    public ToDoItem get(long id) {
        ToDoItem item = new ToDoItem();
        String where = KEY_ID + "=" + String.valueOf(id);
        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);
        if (result.moveToFirst()) {
            item = getRecord(result);
        }
        result.close();
        return item;
    }

    private ToDoItem getRecord(Cursor cursor) {
        ToDoItem result = new ToDoItem();
        result.setId(cursor.getLong(0));
        result.setName(cursor.getString(1));
        result.setIsComplete(cursor.getInt(2)!=0);
        return result;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close(); // ?
        return result;
    }

}