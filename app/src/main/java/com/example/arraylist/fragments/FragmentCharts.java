package com.example.arraylist.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.R;
import com.example.arraylist.items.CompletedTasks;
import com.example.arraylist.items.FailedTasks;
import com.example.arraylist.other.setZeroTimeDate;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCharts extends Fragment{
    private CompletedTasks completedTasks;
    private FailedTasks failedTasks;
    long ONE_DAY_MILLIS = 86400000L;

    public FragmentCharts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        long today = new setZeroTimeDate().transform(new Date()).getTime();
        DBHelper dbHelper = new DBHelper(getContext());
        completedTasks = dbHelper.getCompletedStats(today - ONE_DAY_MILLIS * 6,
                today);
        failedTasks = dbHelper.getFailedStats(today - ONE_DAY_MILLIS * 6,
                today);

        TextView tvAllTasks = view.findViewById(R.id.allTasks);
        tvAllTasks.setText("Всего - " + (completedTasks.count + failedTasks.count));
        TextView tvCompletedTasks = view.findViewById(R.id.completedTasks);
        tvCompletedTasks.setText("Выполненные - " + completedTasks.count);
        TextView tvFailedTasks = view.findViewById(R.id.failedTasks);
        tvFailedTasks.setText("Проваленные - " + failedTasks.count);

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //PieChart
        PieChart pieChart = view.findViewById(R.id.pieChart);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        PieDataSet pieDataSet;
        ArrayList<PieEntry> values = new ArrayList<>();
        if (failedTasks.count != 0 && completedTasks.count != 0) {
            values.add(new PieEntry(completedTasks.count, ""));
            values.add(new PieEntry(failedTasks.count, ""));
            pieDataSet = new PieDataSet(values, "Tasks");
            pieDataSet.setColors(Color.RED, Color.BLUE);
        }
        else if (completedTasks.count == 0) {
            values.add(new PieEntry(failedTasks.count, ""));
            pieDataSet = new PieDataSet(values, "Tasks");
            pieDataSet.setColor(Color.BLUE);
        }
        else if (failedTasks.count == 0) {
            values.add(new PieEntry(completedTasks.count, ""));
            pieDataSet = new PieDataSet(values, "Tasks");
            pieDataSet.setColor(Color.RED);
        }
        else {
            pieDataSet = new PieDataSet(values, "Taks");
        }
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);

        //LineChart
        LineChart lineChart = view.findViewById(R.id.lineChart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);

        ArrayList<ILineDataSet> dataLists = new ArrayList<>();

        ArrayList<Entry> lineDataCompleted = new ArrayList<>();
        for (int i = 0; i < 7; i++)
            lineDataCompleted.add(new Entry(completedTasks.dates.get(i), completedTasks.countPerDay.get(i)));
        LineDataSet lineDataSet = new LineDataSet(lineDataCompleted, "Выполненные");
        lineDataSet.setColors(Color.RED);
        lineDataSet.setLineWidth(2.5f);
        dataLists.add(lineDataSet);

        ArrayList<Entry> lineDataFailed = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            lineDataFailed.add(new Entry(failedTasks.dates.get(i), failedTasks.countPerDay.get(i)));
            Log.e("DEBUG",String.valueOf(i) + " - " +
                    new SimpleDateFormat("dd/MM").format(new Date(failedTasks.dates.get(i))));
        }
        LineDataSet lineDataSet1 = new LineDataSet(lineDataFailed, "Проваленные");
        lineDataSet1.setLineWidth(2.5f);
        lineDataSet1.setColor(Color.BLUE);
        dataLists.add(lineDataSet1);

        LineData data = new LineData(dataLists);

        lineChart.setData(data);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelCount(7, true);
        xAxis.setValueFormatter(new axisValueFormatter());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public class axisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return new SimpleDateFormat("dd/MM").format(new Date((long) value + ONE_DAY_MILLIS));
        }
    }
}
