package com.example.arraylist.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.adapters.MultiTypeTaskAdapter;
import com.example.arraylist.R;
import com.example.arraylist.other.TaskTimeChecker;
import com.example.arraylist.activities.AddActivity;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private ImageButton imageButton;
    private MultiTypeTaskAdapter adapter;

    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imageButton = view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddActivity.class);
                intent.putExtra("type", AddActivity.TYPE_ADD);
                startActivity(intent);
            }
        });

        dbHelper = new DBHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        TaskTimeChecker taskTimeChecker = new TaskTimeChecker(setZeroTimeDate(new Date()).getTime(),
                getContext());
        taskTimeChecker.checkPlannedTasks();
        taskTimeChecker.checkActiveTasks();

        adapter = new MultiTypeTaskAdapter(dbHelper.elementsHome(),
                MultiTypeTaskAdapter.PARENT_HOME, getContext());
        recyclerView.setAdapter(adapter);
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
