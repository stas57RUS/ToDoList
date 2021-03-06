package com.example.arraylist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.R;
import com.example.arraylist.adapters.MultiTypeTaskAdapter;
import com.example.arraylist.other.TaskTimeChecker;
import com.example.arraylist.other.setZeroTimeDate;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlanned extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    public TextView textView;

    public FragmentPlanned() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_tasks, container, false);

        dbHelper = new DBHelper(getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        textView = view.findViewById(R.id.textView);
        textView.setText("Нет запланированных задач.");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        TaskTimeChecker taskTimeChecker = new TaskTimeChecker(new setZeroTimeDate().transform(new Date())
                .getTime(), getContext());
        taskTimeChecker.checkPlannedTasks();
        taskTimeChecker.checkActiveTasks();

        MultiTypeTaskAdapter adapter = new MultiTypeTaskAdapter(dbHelper.getTasks(DBHelper.TABLE_PLANNED),
                MultiTypeTaskAdapter.PARENT_PLANNED, null, null,
                null, this, getContext());
        recyclerView.setAdapter(adapter);
        if (dbHelper.getTasks(DBHelper.TABLE_PLANNED).size() != 0)
            textView.setVisibility(View.GONE);
        else
            textView.setVisibility(View.VISIBLE);
    }
}
