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
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.adapters.MultiTypeTaskAdapter;
import com.example.arraylist.R;
import com.example.arraylist.other.TaskTimeChecker;
import com.example.arraylist.activities.AddActivity;
import com.example.arraylist.other.setZeroTimeDate;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.Calendar;
import java.util.Date;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private TextView textView;

    public FragmentHome() {
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

        ImageButton imageButton = view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddActivity.class);
                intent.putExtra("type", AddActivity.TYPE_ADD);
                startActivity(intent);
            }
        });

        textView = view.findViewById(R.id.textView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        TaskTimeChecker taskTimeChecker = new TaskTimeChecker(new setZeroTimeDate().transform(new Date())
                .getTime(),
                getContext());
        taskTimeChecker.checkPlannedTasks();
        taskTimeChecker.checkActiveTasks();

        final MultiTypeTaskAdapter adapter = new MultiTypeTaskAdapter(dbHelper.elementsHome(),
                MultiTypeTaskAdapter.PARENT_HOME, getContext());
        recyclerView.setAdapter(adapter);
        if (dbHelper.elementsHome().size() != 0)
            textView.setVisibility(View.GONE);
        else {
            textView.setText("Нет текущих задач.");
            textView.setVisibility(View.VISIBLE);
        }
    }
}
