package com.example.arraylist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.other.DateCutter;
import com.example.arraylist.R;
import com.example.arraylist.items.Subtask;
import com.example.arraylist.adapters.SubtaskAdapter;
import com.example.arraylist.items.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private SubtaskAdapter adapter;
    private TextView tvDate;
    private Long today;

    public static final int TYPE_ADD = 1;
    public static final int TYPE_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(getApplicationContext());
        ImageButton imageButton = findViewById(R.id.imageButton);
        tvDate = findViewById(R.id.date);
        ImageView imageDate = findViewById(R.id.imageDate);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        int type = getIntent().getExtras().getInt("type");

        db = dbHelper.getWritableDatabase();
        if (type == TYPE_ADD) {
            ArrayList<String> subtasksRange = new ArrayList<>();
            subtasksRange.add("");
            adapter = new SubtaskAdapter(subtasksRange);
            recyclerView.setAdapter(adapter);

            //Установление даты на текствью датапикера
            Date currentDate = new Date();
            DateFormat formatter = new SimpleDateFormat("dd MMM", Locale.getDefault());
            String dateSelected = formatter.format(currentDate) + " – " + formatter.format(currentDate);
            tvDate.setText(dateSelected);
            //Date
            today = setZeroTimeDate(new Date()).getTime();

            CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
            constraintBuilder.setValidator(DateValidatorPointForward.now());

            MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setCalendarConstraints(constraintBuilder.build());

            final MaterialDatePicker materialDatePicker = builder.build();

            imageDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDatePicker.show(getSupportFragmentManager(), "TAG");
                }
            });
            tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDatePicker.show(getSupportFragmentManager(), "TAG");
                }
            });

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    tvDate.setText(materialDatePicker.getHeaderText());
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Task task = getData();
                    if (!task.task.equals("")) {
                        if (task.dateStart.equals(today))
                            dbHelper.addTableHome(task);
                        else
                            dbHelper.addTablePlanned(task);
                        dbHelper.addTableSubtasks(task.subtasks, task.task);
                    }
                    finish();
                }
            });
        }
        else {
            EditText task_name = findViewById(R.id.task);
            EditText comment = findViewById(R.id.comment);
            TextView date_string = findViewById(R.id.date);

            final Task taskBeforeChanges = dbHelper.getTask(getIntent().getExtras().getLong("task_id"),
                    DBHelper.TABLE_PLANNED);

            task_name.setText(taskBeforeChanges.task);
            comment.setText(taskBeforeChanges.comment);
            date_string.setText(taskBeforeChanges.dateString);

            ArrayList<String> subtasks = transformSubtaksToStrings(taskBeforeChanges.subtasks);
            subtasks.add("");

            adapter = new SubtaskAdapter(subtasks);
            recyclerView.setAdapter(adapter);

            CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
            constraintBuilder.setValidator(DateValidatorPointForward.now());

            MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setCalendarConstraints(constraintBuilder.build());
            builder.setSelection(new Pair<>(taskBeforeChanges.dateStart + 86400000,
                    taskBeforeChanges.dateFinish + 86400000));

            final MaterialDatePicker materialDatePicker = builder.build();

            imageDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDatePicker.show(getSupportFragmentManager(), "TAG");
                }
            });
            tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDatePicker.show(getSupportFragmentManager(), "TAG");
                }
            });

            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object selection) {
                    tvDate.setText(materialDatePicker.getHeaderText());
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Task taskAfterChanges = getData();

                    if (taskAfterChanges.task.equals("")) {
                        dbHelper.deletePlannedTask(taskBeforeChanges.id);
                        dbHelper.deleteSubtasks(dbHelper.getSubtasksIDS(taskBeforeChanges.subtasks));
                    }
                    else {
                        dbHelper.deletePlannedTask(taskBeforeChanges.id);
                        dbHelper.deleteSubtasks(dbHelper.getSubtasksIDS(taskBeforeChanges.subtasks));
                        dbHelper.addTablePlanned(taskAfterChanges);
                    }
                    finish();
                }
            });
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

    private Task getData() {
        TextView tvTask = findViewById(R.id.task),
                tvComment = findViewById(R.id.comment);
        Long firstDate = null, secondDate = null;

        String dateSelected = tvDate.getText().toString();
        DateCutter dateCutter = new DateCutter(dateSelected);
        try {
            firstDate = dateCutter.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            secondDate = dateCutter.getSecond();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String task = tvTask.getText().toString();
        String comment = tvComment.getText().toString();
        String dateString = tvDate.getText().toString();
        Long dateStart = firstDate;
        Long dateFinish = secondDate;

        ArrayList<String> subtasksString = adapter.getElements();
        subtasksString.remove(subtasksString.size() - 1);

        return new Task(null, task, comment, dateString, dateStart, dateFinish,
                transformStringsToSubstasks(subtasksString));
    }

    private ArrayList<Subtask> transformStringsToSubstasks(ArrayList<String> strings) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++)
            subtasks.add(new Subtask(null, strings.get(i)));
        return subtasks;
    }

    private ArrayList<String> transformSubtaksToStrings(ArrayList<Subtask> subtasks) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < subtasks.size(); i++)
            strings.add(subtasks.get(i).task);
        return strings;
    }
}
