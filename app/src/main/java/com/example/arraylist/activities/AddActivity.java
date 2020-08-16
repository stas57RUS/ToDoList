package com.example.arraylist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.other.TranslateStringDateToLong;
import com.example.arraylist.R;
import com.example.arraylist.items.Subtask;
import com.example.arraylist.adapters.SubtaskAdapter;
import com.example.arraylist.items.Task;
import com.example.arraylist.other.setZeroTimeDate;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    private DBHelper dbHelper;
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
        FloatingActionButton FAB = findViewById(R.id.fab);
        tvDate = findViewById(R.id.date);
        ImageView imageDate = findViewById(R.id.imageDate);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        int type = getIntent().getExtras().getInt("type");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
            today = new setZeroTimeDate().transform(new Date()).getTime();

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

            FAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Task task = getData();
                    if (!task.task.equals("")) {
                        if (task.dateStart.equals(today))
                            dbHelper.addTask(task, DBHelper.TABLE_ACTIVE);
                        else
                            dbHelper.addTask(task, DBHelper.TABLE_ACTIVE);
                        dbHelper.addSubtasks(task.subtasks, task.task);
                    }
                    finish();
                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                FAB.setImageDrawable(getResources().getDrawable(R.drawable.ic_done, this.getTheme()));
            }
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

            FAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Task taskAfterChanges = getData();

                    if (taskAfterChanges.task.equals("")) {
                        dbHelper.deleteTask(taskBeforeChanges.id, DBHelper.TABLE_PLANNED);
                        dbHelper.deleteSubtasks(dbHelper.getSubtasksIDS(taskBeforeChanges.subtasks));
                    }
                    else {
                        dbHelper.deleteTask(taskBeforeChanges.id, DBHelper.TABLE_PLANNED);
                        dbHelper.deleteSubtasks(dbHelper.getSubtasksIDS(taskBeforeChanges.subtasks));
                        dbHelper.addTask(taskAfterChanges, DBHelper.TABLE_PLANNED);
                    }
                    finish();
                }
            });
        }
    }

    private Task getData() {
        TextView tvTask = findViewById(R.id.task),
                tvComment = findViewById(R.id.comment);
        Long firstDate = null, secondDate = null;

        String dateSelected = tvDate.getText().toString();
        TranslateStringDateToLong dateCutter = new TranslateStringDateToLong(dateSelected);
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
