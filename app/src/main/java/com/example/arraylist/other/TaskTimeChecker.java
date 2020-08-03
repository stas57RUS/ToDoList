package com.example.arraylist.other;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.items.Task;

public class TaskTimeChecker {
    Long today;
    DBHelper dbHelper;
    SQLiteDatabase db;

    public TaskTimeChecker(Long today, Context context) {
        this.today = today;
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void checkPlannedTasks() {
        String[] selectionArgs = new String[] { today.toString() };

        Cursor cursor = db.query("planned", null,
                "date_start <= ?", selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                dbHelper.addTableHome(new Task(null,
                        cursor.getString(cursor.getColumnIndex("task")),
                        cursor.getString(cursor.getColumnIndex("comment")),
                        cursor.getString(cursor.getColumnIndex("date_string")),
                        cursor.getLong(cursor.getColumnIndex("date_start")),
                        cursor.getLong(cursor.getColumnIndex("date_finish")),
                        null));
                dbHelper.deletePlannedTask(cursor.getLong(cursor.getColumnIndex("_id")));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void checkActiveTasks() {
        String[] selectionArgs = new String[] { today.toString() };

        Cursor cursor = db.query("home", null,
                "date_finish < ?", selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                dbHelper.addTableFailed(new Task(null,
                        cursor.getString(cursor.getColumnIndex("task")),
                        cursor.getString(cursor.getColumnIndex("comment")),
                        cursor.getString(cursor.getColumnIndex("date_string")),
                        cursor.getLong(cursor.getColumnIndex("date_start")),
                        cursor.getLong(cursor.getColumnIndex("date_finish")),
                        null));
                dbHelper.deleteHomeTask(cursor.getLong(cursor.getColumnIndex("_id")));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

}
