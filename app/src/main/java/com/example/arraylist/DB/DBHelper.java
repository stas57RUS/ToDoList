package com.example.arraylist.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.example.arraylist.items.CompletedTasks;
import com.example.arraylist.items.FailedTasks;
import com.example.arraylist.items.Subtask;
import com.example.arraylist.items.Task;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database";

    public static final String TABLE_ACTIVE = "active";
    public static final String TABLE_COMPLETED = "complete";
    public static final String TABLE_FAILED = "failed";
    public static final String TABLE_PLANNED = "planned";
    public static final String TABLE_SUBTASKS = "table_subtasks";
    public static final String TABLE_ALARM_STATE = "alarm_state";
    public static final String TABLE_STATS = "stats";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_DATE_STRING = "date_string";
    public static final String COLUMN_DATE_START = "date_start";
    public static final String COLUMN_DATE_FINISH = "date_finish";

    public static final String COLUMN_SUBTASKS = "column_subtasks";
    public static final String COLUMN_CHECKED = "checked";
    public static final String COLUMN_KEY = "column_key";

    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_TIME_HOURS = "time_hours";
    public static final String COLUMN_TIME_MINUTES = "time_minutes";

    public static final String COLUMN_STATS_DATE = "stats_date";
    public static final String COLUMN_COM_OR_FAIL = "completed_or_failed";

    public static final int ALARM_STATE_RUNNING = 101;
    public static final int ALARM_STATE_NOT_WORKING = 102;
    public static final int ALARM_STATE_WAITING_START = 103;
    public static final int ALARM_STATE_WAITING_STOP = 104;
    public static final int ALARM_STATE_WAITING_UPDATE = 105;

    public static final int ALARM_HOURS = 111;
    public static final int ALARM_MINUTES = 222;

    public static final int STATS_TYPE_COMPLETED = 333;
    public static final int STATS_TYPE_FAILED = 444;

    private SQLiteDatabase db = this.getWritableDatabase();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ACTIVE + "(" + COLUMN_ID + " Long primary key," +
                COLUMN_TASK + " text," + COLUMN_COMMENT + " text," + COLUMN_DATE_STRING + " text," +
                COLUMN_DATE_START + " long," + COLUMN_DATE_FINISH + " long" + ")");

        db.execSQL("create table " + TABLE_COMPLETED + "(" + COLUMN_ID + " Long primary key," +
                COLUMN_TASK + " text," + COLUMN_COMMENT + " text," + COLUMN_DATE_STRING + " text," +
                COLUMN_DATE_START + " long," + COLUMN_DATE_FINISH + " long" + ")");

        db.execSQL("create table " + TABLE_FAILED + "(" + COLUMN_ID + " Long primary key," +
                COLUMN_TASK + " text," + COLUMN_COMMENT + " text," + COLUMN_DATE_STRING + " text," +
                COLUMN_DATE_START + " long," + COLUMN_DATE_FINISH + " long" + ")");

        db.execSQL("create table " + TABLE_PLANNED + "(" + COLUMN_ID + " Long primary key," +
                COLUMN_TASK + " text," + COLUMN_COMMENT + " text," + COLUMN_DATE_STRING + " text," +
                COLUMN_DATE_START + " long," + COLUMN_DATE_FINISH + " long" + ")");

        db.execSQL("create table " + TABLE_SUBTASKS + "(" + COLUMN_ID + " Long primary key," +
                COLUMN_SUBTASKS + " text," + COLUMN_CHECKED + " boolean," +
                COLUMN_KEY + " text" + ")"); // COLUMN_KEY = PARENT TASK NAME

        db.execSQL("create table " + TABLE_ALARM_STATE + "(" + COLUMN_STATE + " int," + COLUMN_TIME_HOURS
                + " int," + COLUMN_TIME_MINUTES + " int" + ")");

        db.execSQL("create table " + TABLE_STATS + "(" + COLUMN_STATS_DATE + " long," + COLUMN_COM_OR_FAIL
                + " int" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addNewStats(Long date, int statsType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATS_DATE, date);
        values.put(COLUMN_COM_OR_FAIL, statsType);
        db.insert(TABLE_STATS, null, values);
    }

    public CompletedTasks getCompletedStats(Long dateStart, Long dateFinish) {
        ArrayList<Long> dates = new ArrayList<>();
        ArrayList<Integer> countPerDay = new ArrayList<>();
        long ONE_DAY_MILLIS = 86400000L;
        for (long date = dateStart; date <= dateFinish; date += ONE_DAY_MILLIS) {
            String[] selectionArgs = new String[] {String.valueOf(date), String.valueOf(STATS_TYPE_COMPLETED)};
            Cursor cursor = db.query(TABLE_STATS, null,
                    "stats_date = ? AND completed_or_failed = ?", selectionArgs,
                    null, null, null);
            if (cursor.moveToFirst())
                countPerDay.add(cursor.getCount());
            else
                countPerDay.add(0);
            cursor.close();
            dates.add(date);
        }
        return new CompletedTasks(dates, countPerDay);
    }

    public FailedTasks getFailedStats(Long dateStart, Long dateFinish) {
        ArrayList<Long> dates = new ArrayList<>();
        ArrayList<Integer> countPerDay = new ArrayList<>();
        long ONE_DAY_MILLIS = 86400000L;
        for (long date = dateStart; date <= dateFinish; date += ONE_DAY_MILLIS) {
            String[] selectionArgs = new String[]{String.valueOf(date), String.valueOf(STATS_TYPE_FAILED)};
            Cursor cursor = db.query(TABLE_STATS, null,
                    "stats_date = ? AND completed_or_failed = ?", selectionArgs,
                    null, null, null);
            if (cursor.moveToFirst())
                countPerDay.add(cursor.getCount());
            else
                countPerDay.add(0);
            cursor.close();
            dates.add(date);
        }
        return new FailedTasks(dates, countPerDay);
    }

    public int getAlarmSate(){
        int state;
        Cursor cursor = db.query(TABLE_ALARM_STATE, null, null,
                null, null, null, null);
        if(cursor.moveToFirst())
            state = cursor.getInt(cursor.getColumnIndex(COLUMN_STATE));
        //Если в таблице нет значений(первый запуск)
        else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATE, ALARM_STATE_WAITING_START);
            values.put(COLUMN_TIME_HOURS, 6);
            values.put(COLUMN_TIME_MINUTES, 30);
            db.insert(TABLE_ALARM_STATE, null, values);
            state = ALARM_STATE_WAITING_START;
        }
        cursor.close();
        return state;
    }

    public int getAlarmTime(int key) {
        int time;
        Cursor cursor = db.query(TABLE_ALARM_STATE, null, null, null,
                null, null, null);
        cursor.moveToFirst();
        if (key == ALARM_HOURS)
            time = cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_HOURS));
        else
            time = cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_MINUTES));
        cursor.close();
        return time;
    }

    public void updateAlarmState(int state){
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATE, state);
        db.update(TABLE_ALARM_STATE, values, null, null);
    }

    public void updateAlarmTime(int hours, int minutes) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME_HOURS, hours);
        values.put(COLUMN_TIME_MINUTES, minutes);
        db.update(TABLE_ALARM_STATE, values, null, null);
    }

    public int newActiveTasks(long date, String table){
        int count;
        String[] selectionArgs = new String[] {String.valueOf(date)};
        Cursor cursor = db.query(table, null,
                "date_start = ?", selectionArgs, null, null, null);
        cursor.moveToFirst();
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Long getFreeId(String table) {
        long freeId;
        Cursor cursor = db.query(table, null, null, null, null,
                null, null);
        if (cursor.moveToLast())
            freeId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)) + 1;
        else
            freeId = 0;
        cursor.close();
        return freeId;
    }

    public Boolean getSubtaskChecked(Long id) {
        boolean checked;
        String[] selectionArgs = new String[] {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_SUBTASKS, null,
                "_id = ?", selectionArgs, null, null, null);
        cursor.moveToFirst();

        checked = cursor.getString(cursor.getColumnIndex(COLUMN_CHECKED)).equals("1");
        cursor.close();

        return checked;
    }

    public ArrayList<Long> getSubtasksIDS(ArrayList<Subtask> subtasks) {
        ArrayList<Long> subtasksIDS = new ArrayList<>();
        for (int i = 0; i < subtasks.size(); i++) {
            subtasksIDS.add(subtasks.get(i).id);
        }
        return subtasksIDS;
    }

    public Task getTask(Long id, String table) {
        String task_name, comment, date_string;
        Long date_start, date_finish;
        ArrayList<Subtask> subtasks;

        String[] selectionArgs = new String[] {String.valueOf(id)};
        Cursor cursor = db.query(table, null, "_id = ?", selectionArgs,
                null, null, null);
        if (cursor.moveToFirst()) {
            task_name = cursor.getString(cursor.getColumnIndex(COLUMN_TASK));
            comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
            date_string = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_STRING));
            date_start = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_START));
            date_finish = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FINISH));
            subtasks = getSubtasks(task_name);
        }
        else {
            task_name = null;
            comment = null;
            date_string = null;
            date_start = null;
            date_finish = null;
            subtasks = null;
        }
        cursor.close();
        return new Task(id, task_name, comment, date_string, date_start, date_finish, subtasks);
    }

    public ArrayList<Task> getTasks(String table) {
        ArrayList<Task> tempElements = new ArrayList<>();
        Cursor cursor = db.query(table, null, null, null,
                null, null, null);
        if(cursor.moveToFirst()){
            do{
                Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String task = cursor.getString(cursor.getColumnIndex(COLUMN_TASK));
                String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
                String dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_STRING));
                Long dateStart = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_START));
                Long dateFinish = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FINISH));
                ArrayList<Subtask> subtasks = getSubtasks(task);
                tempElements.add(new Task(id, task, comment, dateString,
                        dateStart, dateFinish, subtasks));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return tempElements;
    }

    public ArrayList<Subtask> getSubtasks(String key) {
        ArrayList<Subtask> tempElements = new ArrayList<>();
        String[] selectionArgs = new String[] {key};
        Cursor cursor = db.query(TABLE_SUBTASKS, null,
                "column_key = ?", selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                tempElements.add(new Subtask(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_SUBTASKS))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tempElements;
    }

    public void addTask(Task item, String table) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getFreeId(table));
        values.put(COLUMN_TASK, item.task);
        values.put(COLUMN_COMMENT, item.comment);
        values.put(COLUMN_DATE_STRING, item.dateString);
        values.put(COLUMN_DATE_START, item.dateStart);
        values.put(COLUMN_DATE_FINISH, item.dateFinish);
        db.insert(table, null, values);
    }

    public void addSubtasks(@NonNull ArrayList<Subtask> subtasks,
                            String key){
        for (int i = 0; i < subtasks.size(); i++){
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, getFreeId(TABLE_SUBTASKS));
            values.put(COLUMN_SUBTASKS, subtasks.get(i).task);
            values.put(COLUMN_CHECKED, false);
            values.put(COLUMN_KEY, key);
            db.insert(TABLE_SUBTASKS, null, values);
        }
    }

    public void updateTableSubtask_checkBox(Long id, Boolean state) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHECKED, !state);
        db.update(TABLE_SUBTASKS, values, COLUMN_ID + " = ?",
                new String[] {String.valueOf(id)});
    }

    public void deleteTask(Long id, String table) {
        db.delete(table, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }

    public void deleteSubtasks(ArrayList<Long> id){
        for (int i = 0; i < id.size(); i++) {
            db.delete(TABLE_SUBTASKS, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id.get(i))});
        }
    }

    public void deleteStats() {
        db.delete(TABLE_STATS, null, null);
    }
}
