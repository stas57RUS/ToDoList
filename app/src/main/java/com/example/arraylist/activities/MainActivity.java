package com.example.arraylist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.fragments.FragmentCharts;
import com.example.arraylist.fragments.FragmentComplete;
import com.example.arraylist.fragments.FragmentFailed;
import com.example.arraylist.fragments.FragmentHome;
import com.example.arraylist.fragments.FragmentPlanned;
import com.example.arraylist.R;
import com.example.arraylist.scheduledNotification.AlarmHelper;
import com.example.arraylist.scheduledNotification.TimeNotification;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton FAB = findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("type", AddActivity.TYPE_ADD);
                startActivity(intent);
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome())
                .commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        AlarmHelper alarmHelper = new AlarmHelper(getApplicationContext());
        alarmHelper.run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String aboutAuthor = "Приложение сдалано учащимся 40 лицея, Ершовым Станиславом.";
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).
                setMessage(aboutAuthor)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.author:
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectFragment = null;

                    switch (item.getItemId()) {
                        case R.id.home:
                            selectFragment = new FragmentHome();
                            break;
                        case R.id.completed:
                            selectFragment = new FragmentComplete();
                            break;
                        case R.id.failed:
                            selectFragment = new FragmentFailed();
                            break;
                        case R.id.planned:
                            selectFragment = new FragmentPlanned();
                            break;
                        case R.id.charts:
                            selectFragment = new FragmentCharts();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectFragment)
                            .commit();
                    return true;
                }
            };
}
