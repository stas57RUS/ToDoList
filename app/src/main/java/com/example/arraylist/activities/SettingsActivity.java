package com.example.arraylist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.R;
import com.example.arraylist.other.TimePicker;
import com.example.arraylist.scheduledNotification.AlarmHelper;

public class SettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dbHelper = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvTimePickerTime = findViewById(R.id.tvTimePickerTime);
        tvTimePickerTime.setText("После - " + dbHelper.getAlarmHours() + ":" +
                dbHelper.getAlarmMinutes());

        RelativeLayout timePickerButton = findViewById(R.id.timePicker);
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePicker(dbHelper.getAlarmHours(),
                        dbHelper.getAlarmMinutes());
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int hours, int minutes) {
        TextView alarmTime = findViewById(R.id.tvTimePickerTime);
        if (minutes < 10)
            alarmTime.setText("После - " + hours + ":0" + minutes);
        else
            alarmTime.setText("После - " + hours + ":" + minutes);

        dbHelper.updateAlarmTime(hours, minutes);
        dbHelper.updateAlarmState(DBHelper.ALARM_STATE_WAITING_UPDATE);

        AlarmHelper alarmHelper = new AlarmHelper(getApplicationContext());
        alarmHelper.run();
    }
}