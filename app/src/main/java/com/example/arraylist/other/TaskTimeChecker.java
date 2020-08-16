package com.example.arraylist.other;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.items.Task;

import java.util.Date;

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

        Cursor cursor = db.query(DBHelper.TABLE_PLANNED, null,
                "date_start <= ?", selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                dbHelper.addTask(new Task(null,
                        cursor.getString(cursor.getColumnIndex("task")),
                        cursor.getString(cursor.getColumnIndex("comment")),
                        cursor.getString(cursor.getColumnIndex("date_string")),
                        cursor.getLong(cursor.getColumnIndex("date_start")),
                        cursor.getLong(cursor.getColumnIndex("date_finish")),
                        null), DBHelper.TABLE_ACTIVE);
                dbHelper.deleteTask(cursor.getLong(cursor.getColumnIndex("_id")),
                        DBHelper.TABLE_PLANNED);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void checkActiveTasks() {
        String[] selectionArgs = new String[] { today.toString() };

        Cursor cursor = db.query(DBHelper.TABLE_ACTIVE, null,
                "date_finish < ?", selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                dbHelper.addTask(new Task(null,
                        cursor.getString(cursor.getColumnIndex("task")),
                        cursor.getString(cursor.getColumnIndex("comment")),
                        cursor.getString(cursor.getColumnIndex("date_string")),
                        cursor.getLong(cursor.getColumnIndex("date_start")),
                        cursor.getLong(cursor.getColumnIndex("date_finish")),
                        null), DBHelper.TABLE_FAILED);
                dbHelper.deleteTask(cursor.getLong(cursor.getColumnIndex("_id")),
                        DBHelper.TABLE_ACTIVE);
                dbHelper.addNewStats(new setZeroTimeDate().transform(new Date()).getTime(),
                        DBHelper.STATS_TYPE_FAILED);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

}
