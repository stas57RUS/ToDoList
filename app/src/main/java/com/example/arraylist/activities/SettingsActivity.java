package com.example.arraylist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.R;
import com.example.arraylist.other.TimePicker;
import com.example.arraylist.scheduledNotification.AlarmHelper;

import org.w3c.dom.Text;

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

        Switch switch1 = findViewById(R.id.switch1);
        switch1.setChecked(getChecked());
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    dbHelper.updateAlarmState(DBHelper.ALARM_STATE_WAITING_START);
                else
                    dbHelper.updateAlarmState(DBHelper.ALARM_STATE_WAITING_STOP);
                AlarmHelper alarmHelper = new AlarmHelper(getApplicationContext());
                alarmHelper.run();
            }
        });

        final int hours = DBHelper.ALARM_HOURS, minutes = DBHelper.ALARM_MINUTES;
        TextView tvTimePickerTime = findViewById(R.id.tvTimePickerTime);
        tvTimePickerTime.setText("После - " + dbHelper.getAlarmTime(hours) + ":" +
                dbHelper.getAlarmTime(minutes));

        RelativeLayout timePickerButton = findViewById(R.id.timePicker);
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePicker(dbHelper.getAlarmTime(hours),
                        dbHelper.getAlarmTime(minutes));
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        TextView tvDeleteStats = findViewById(R.id.tvDeleteStats);
        tvDeleteStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this).
                        setMessage("Данные будет нельзя восстановить. Удалить?")
                        .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper dbHelper = new DBHelper(SettingsActivity.this);
                                dbHelper.deleteStats();
                                Toast.makeText(SettingsActivity.this,"Данные удалены.",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                dialog.show();
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

    private boolean getChecked() {
        return dbHelper.getAlarmSate() == DBHelper.ALARM_STATE_RUNNING;
    }
}