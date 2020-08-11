package com.example.arraylist.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.github.mikephil.charting.components.YAxis;
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
public class FragmentCharts extends Fragment implements AdapterView.OnItemSelectedListener {
    private long ONE_DAY_MILLIS = 86400000L;
    private long today;
    private DBHelper dbHelper;

    public FragmentCharts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        today = new setZeroTimeDate().transform(new Date()).getTime();
        dbHelper = new DBHelper(getContext());

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        changeView(view, 7, 7);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String selectedItem = (String) adapterView.getItemAtPosition(position);
        switch (selectedItem) {
            case "7 дней":
                changeView(getView(), 7, 7);
                break;
            case "14 дней":
                changeView(getView(), 14, 7);
                break;
            case "месяц":
                changeView(getView(), 30, 10);
                break;
            case "3 месяца":
                changeView(getView(), 91, 10);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class XAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return new SimpleDateFormat("dd/MM").format(new Date((long) value + ONE_DAY_MILLIS));
        }
    }

    private void changeView(View view, int period, int labelCountXAxis) {
        CompletedTasks completedTasks = dbHelper.getCompletedStats(today - ONE_DAY_MILLIS *
                (period - 1), today);
        FailedTasks failedTasks = dbHelper.getFailedStats(today - ONE_DAY_MILLIS *
                (period - 1), today);

        TextView tvAllTasks = view.findViewById(R.id.allTasks);
        tvAllTasks.setText("Всего - " + (completedTasks.count + failedTasks.count));
        TextView tvCompletedTasks = view.findViewById(R.id.completedTasks);
        tvCompletedTasks.setText("Выполненные - " + completedTasks.count);
        TextView tvFailedTasks = view.findViewById(R.id.failedTasks);
        tvFailedTasks.setText("Проваленные - " + failedTasks.count);
        TextView textView = view.findViewById(R.id.textView);
        if (completedTasks.count + failedTasks.count != 0) {
            setUpPieChart(view, completedTasks, failedTasks);
            setUpLineChart(view, completedTasks, failedTasks, period, labelCountXAxis);
            tvAllTasks.setVisibility(View.VISIBLE);
            tvCompletedTasks.setVisibility(View.VISIBLE);
            tvFailedTasks.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
        else {
            PieChart pieChart = view.findViewById(R.id.pieChart);
            pieChart.setVisibility(View.INVISIBLE);
            LineChart lineChart = view.findViewById(R.id.lineChart);
            lineChart.setVisibility(View.INVISIBLE);
            tvAllTasks.setVisibility(View.INVISIBLE);
            tvCompletedTasks.setVisibility(View.INVISIBLE);
            tvFailedTasks.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void setUpPieChart(View view, CompletedTasks completedTasks,
                               FailedTasks failedTasks) {
        PieChart pieChart = view.findViewById(R.id.pieChart);
        pieChart.setVisibility(View.VISIBLE);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        PieDataSet pieDataSet;
        ArrayList<PieEntry> values = new ArrayList<>();
        if (completedTasks.count == 0) {
            values.add(new PieEntry(failedTasks.count, ""));
            pieDataSet = new PieDataSet(values, "Tasks");
            pieDataSet.setColor(Color.BLUE);
        } else if (failedTasks.count == 0) {
            values.add(new PieEntry(completedTasks.count, ""));
            pieDataSet = new PieDataSet(values, "Tasks");
            pieDataSet.setColor(Color.RED);
        } else {
            values.add(new PieEntry(completedTasks.count, ""));
            values.add(new PieEntry(failedTasks.count, ""));
            pieDataSet = new PieDataSet(values, "Tasks");
            pieDataSet.setColors(Color.RED, Color.BLUE);
        }
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void setUpLineChart(View view, CompletedTasks completedTasks, FailedTasks failedTasks,
                                int period, int labelCountXAxis) {
        LineChart lineChart = view.findViewById(R.id.lineChart);
        lineChart.setVisibility(View.VISIBLE);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setFormSize(14f);
        lineChart.getLegend().setTextSize(14f);
        lineChart.getAxisRight().setEnabled(false);

        ArrayList<ILineDataSet> dataLists = new ArrayList<>();

        ArrayList<Entry> lineDataCompleted = new ArrayList<>();
        for (int i = 0; i < period; i++)
            lineDataCompleted.add(new Entry(completedTasks.dates.get(i), completedTasks.countPerDay.get(i)));
        LineDataSet lineDataSet = new LineDataSet(lineDataCompleted, "Выполненные");
        lineDataSet.setColors(Color.RED);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2.5f);
        dataLists.add(lineDataSet);

        ArrayList<Entry> lineDataFailed = new ArrayList<>();
        for (int i = 0; i < period; i++)
            lineDataFailed.add(new Entry(failedTasks.dates.get(i), failedTasks.countPerDay.get(i)));
        LineDataSet lineDataSet1 = new LineDataSet(lineDataFailed, "Проваленные");
        lineDataSet1.setLineWidth(2.5f);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setColor(Color.BLUE);
        dataLists.add(lineDataSet1);

        LineData data = new LineData(dataLists);

        lineChart.setData(data);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter());
        xAxis.setLabelCount(labelCountXAxis, true);
        xAxis.setTextSize(11f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGranularity(1f);
        yAxis.setTextSize(11f);

        lineChart.invalidate();
    }
}
