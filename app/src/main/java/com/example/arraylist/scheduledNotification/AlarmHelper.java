package com.example.arraylist.scheduledNotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.arraylist.DB.DBHelper;

import java.util.Calendar;

public class AlarmHelper {
    private Context context;
    private DBHelper dbHelper;

    public AlarmHelper(Context context) {
        this.context = context;
    }

    public void run() {
        dbHelper = new DBHelper(context);
        int alarmState = dbHelper.getAlarmSate();
        switch (alarmState) {
            case DBHelper.ALARM_STATE_WAITING_START:
            case DBHelper.ALARM_STATE_WAITING_UPDATE:
                setAlarm();
                dbHelper.updateAlarmState(DBHelper.ALARM_STATE_RUNNING);
                break;
            case DBHelper.ALARM_STATE_WAITING_STOP:
                cancelAlarm();
                dbHelper.updateAlarmState(DBHelper.ALARM_STATE_NOT_WORKING);
                break;
        }
    }

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TimeNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, dbHelper.getAlarmTime(DBHelper.ALARM_HOURS));
        calendar.set(Calendar.MINUTE, dbHelper.getAlarmTime(DBHelper.ALARM_MINUTES));

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TimeNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
}
