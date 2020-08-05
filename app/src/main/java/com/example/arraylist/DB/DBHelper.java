package com.example.arraylist.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.example.arraylist.items.Subtask;
import com.example.arraylist.items.Task;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tasks";

    public static final String TABLE_HOME = "home";
    public static final String TABLE_COMPLETE = "complete";
    public static final String TABLE_FAILED = "failed";
    public static final String TABLE_PLANNED = "planned";
    public static final String TABLE_SUBTASKS = "table_subtasks";
    public static final String TABLE_ALARM_STATE = "alarm_state";

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

    public static final int ALARM_STATE_RUNNING = 101;
    public static final int ALARM_STATE_NOT_WORKING = 102;
    public static final int ALARM_STATE_WAITING_FOR_START = 103;
    public static final int ALARM_STATE_WAITING_FOR_STOP = 104;

    private SQLiteDatabase db = this.getWritableDatabase();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_HOME + "(" + COLUMN_ID + " Long primary key," +
                COLUMN_TASK + " text," + COLUMN_COMMENT + " text," + COLUMN_DATE_STRING + " text," +
                COLUMN_DATE_START + " long," + COLUMN_DATE_FINISH + " long" + ")");

        db.execSQL("create table " + TABLE_COMPLETE + "(" + COLUMN_ID + " Long primary key," +
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
        db.execSQL("create table " + TABLE_ALARM_STATE + "(" + COLUMN_STATE + " int" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Long getTaskId(String task, String table){
        Long id;
        String[] selectionArgs = new String[] {task};
        Cursor cursor = db.query(table, null,
                "task = ?", selectionArgs, null, null, null);
        if (cursor.moveToFirst())
            id = cursor.getLong(cursor.getColumnIndex("_id"));
        else
            id = null;
        cursor.close();
        return id;
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
            values.put(COLUMN_STATE, ALARM_STATE_WAITING_FOR_START);
            db.insert(TABLE_ALARM_STATE, null, values);
            state = ALARM_STATE_WAITING_FOR_START;
        }
        cursor.close();
        return state;
    }

    public void changeAlamState(int state){
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATE, state);
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
        Long freeId;
        Cursor cursor = db.query(table, null, null, null, null,
                null, null);
        if (cursor.moveToLast())
            freeId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)) + 1;
        else
            freeId = (long) 0;
        cursor.close();
        return freeId;
    }

    public Boolean getSubtaskChecked(Long id) {
        Boolean checked;
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
            subtasks = elementsSubtask(task_name);
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

    public ArrayList<Task> elementsHome(){
        ArrayList<Task> tempElements = new ArrayList<>();
        Cursor cursor = db.query(TABLE_HOME, null, null, null,
                null, null, null);
        if(cursor.moveToFirst()){
            do{
                Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String task = cursor.getString(cursor.getColumnIndex(COLUMN_TASK));
                String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
                String dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_STRING));
                Long dateStart = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_START));
                Long dateFinish = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FINISH));
                ArrayList<Subtask> subtasks = elementsSubtask(task);
                tempElements.add(new Task(id, task, comment, dateString,
                        dateStart, dateFinish, subtasks));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return tempElements;
    }

    public ArrayList<Task> elementsComplete(){
        String sql = "select * from " + TABLE_COMPLETE;
        ArrayList<Task> tempElements = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String task = cursor.getString(cursor.getColumnIndex(COLUMN_TASK));
                String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
                String dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_STRING));
                Long dateStart = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_START));
                Long dateFinish = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FINISH));
                ArrayList<Subtask> subtasks = elementsSubtask(task);
                tempElements.add(new Task(id, task, comment, dateString,
                        dateStart, dateFinish, subtasks));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return tempElements;
    }

    public ArrayList<Task> elementsFailed(){
        String sql = "select * from " + TABLE_FAILED;
        ArrayList<Task> tempElements = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String task = cursor.getString(cursor.getColumnIndex(COLUMN_TASK));
                String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
                String dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_STRING));
                Long dateStart = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_START));
                Long dateFinish = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FINISH));
                ArrayList<Subtask> subtasks = elementsSubtask(task);
                tempElements.add(new Task(id, task, comment, dateString,
                        dateStart, dateFinish, subtasks));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return tempElements;
    }

    public ArrayList<Task> elementsPlanned(){
        String sql = "select * from " + TABLE_PLANNED;
        ArrayList<Task> tempElements = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String task = cursor.getString(cursor.getColumnIndex(COLUMN_TASK));
                String comment = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT));
                String dateString = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_STRING));
                Long dateStart = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_START));
                Long dateFinish = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FINISH));
                ArrayList<Subtask> subtasks = elementsSubtask(task);
                tempElements.add(new Task(id, task, comment, dateString,
                        dateStart, dateFinish, subtasks));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return tempElements;
    }

    public ArrayList<Subtask> elementsSubtask(String key) {
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

    public void addTableHome(@NonNull Task item){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getFreeId(TABLE_HOME));
        values.put(COLUMN_TASK, item.task);
        values.put(COLUMN_COMMENT, item.comment);
        values.put(COLUMN_DATE_STRING, item.dateString);
        values.put(COLUMN_DATE_START, item.dateStart);
        values.put(COLUMN_DATE_FINISH, item.dateFinish);
        db.insert(TABLE_HOME, null, values);
    }

    public void addTableComplete(@NonNull Task item){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getFreeId(TABLE_COMPLETE));
        values.put(COLUMN_TASK, item.task);
        values.put(COLUMN_COMMENT, item.comment);
        values.put(COLUMN_DATE_STRING, item.dateString);
        values.put(COLUMN_DATE_START, item.dateStart);
        values.put(COLUMN_DATE_FINISH, item.dateFinish);
        db.insert(TABLE_COMPLETE, null, values);
    }

    public void addTableFailed(@NonNull Task item){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getFreeId(TABLE_FAILED));
        values.put(COLUMN_TASK, item.task);
        values.put(COLUMN_COMMENT, item.comment);
        values.put(COLUMN_DATE_STRING, item.dateString);
        values.put(COLUMN_DATE_START, item.dateStart);
        values.put(COLUMN_DATE_FINISH, item.dateFinish);
        db.insert(TABLE_FAILED, null, values);
    }

    public void addTablePlanned(@NonNull Task item){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getFreeId(TABLE_PLANNED));
        values.put(COLUMN_TASK, item.task);
        values.put(COLUMN_COMMENT, item.comment);
        values.put(COLUMN_DATE_STRING, item.dateString);
        values.put(COLUMN_DATE_START, item.dateStart);
        values.put(COLUMN_DATE_FINISH, item.dateFinish);
        db.insert(TABLE_PLANNED, null, values);
    }

    public void addTableSubtasks(@NonNull ArrayList<Subtask> subtasks,
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

//    public void updateTableSubtasks_Key(Long oldKey, Long newKey) { // Замена всех элементов с переданным ID
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_KEY, newKey);
//        db.update(TABLE_SUBTASKS, values, COLUMN_KEY + " = ?",
//                new String[] {String.valueOf(oldKey)});
//    }

    public void updateTableSubtasks_checkBox(Long id, Boolean state) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHECKED, !state);
        db.update(TABLE_SUBTASKS, values, COLUMN_ID + " = ?",
                new String[] {String.valueOf(id)});
    }

    public void deleteHomeTask(Long id){
        db.delete(TABLE_HOME, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }

    public void deleteCompleteTask(Long id){
        db.delete(TABLE_COMPLETE, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }

    public void deleteFaildTask(Long id){
        db.delete(TABLE_FAILED, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }

    public void deletePlannedTask(Long id){
        db.delete(TABLE_PLANNED, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }

    public void deleteSubtasks(ArrayList<Long> id){
        for (int i = 0; i < id.size(); i++) {
            db.delete(TABLE_SUBTASKS, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id.get(i))});
        }
    }

//    public void deleteSubtasks(ArrayList<Long> id){
//        db.delete(TABLE_SUBTASKS, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
//    }
}
