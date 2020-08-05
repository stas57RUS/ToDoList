package com.example.arraylist.scheduledNotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.arraylist.DB.DBHelper;

import java.util.Calendar;

public class StartAlarm {
    private Context context;

    public StartAlarm(Context context) {
        this.context = context;
    }

    public void start() {
        DBHelper dbHelper = new DBHelper(context);
        int alarmState = dbHelper.getAlarmSate();
        if (alarmState == DBHelper.ALARM_STATE_WAITING_FOR_START) {

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, TimeNotification.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 6);
            calendar.set(Calendar.MINUTE, 30);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);


            dbHelper.changeAlamState(DBHelper.ALARM_STATE_RUNNING);

        }
    }
}
