package com.example.arraylist.scheduledNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.R;
import com.example.arraylist.activities.MainActivity;

import java.util.Calendar;
import java.util.Date;

public class TimeNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int count = 0;
        DBHelper dbHelper = new DBHelper(context);
        count += dbHelper.newActiveTasks(setZeroTimeDate(new Date()).getTime(), DBHelper.TABLE_PLANNED);
        count += dbHelper.newActiveTasks(setZeroTimeDate(new Date()).getTime(), DBHelper.TABLE_HOME);

        createNotificationChannel(context);

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (count != 0) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context, "notifyChanelId")
                    .setSmallIcon(R.drawable.ic_stat_done_outline)
                    .setContentTitle(getContextText(count))
                    .setContentText("Нажмите, чтобы открыть приложение.")
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat
                    .from(context);

            notificationManager.notify(1337, builder.build());
        }
    }

    private String getContextText(int count){
        String s;
        String countS = String.valueOf(count);
        if (count % 10 == 1 && count / 10 == 0)
            s = "Не забудь! У тебя 1 новая задача.";
        else if (count % 100 == 11 || count % 100 == 12 || count % 100 == 13 ||
                count % 100 == 14)
            s = "Не забудь! У тебя " + countS + " новых задач.";
        else if (count % 10 == 2 || count % 10 == 3 || count % 10 == 4)
            s = "Не забудь! У тебя " + countS + " новые задачи.";
        else if (count % 10 == 1)
            s = "Не забудь! У тебя " + countS + " новая задача.";
        else
            s = "Не забудь! У тебя " + countS + " новых задач.";
        return s;
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "TaskNotificationChannel";
            String descriprion = "Channel for tasks-__-";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("notifyChanelId",
                    name, importance);
            notificationChannel.setDescription(descriprion);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Date setZeroTimeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }
}
