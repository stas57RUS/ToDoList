package com.example.arraylist.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.arraylist.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCharts extends Fragment{

    public FragmentCharts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        PieChart pieChart = view.findViewById(R.id.pieChart);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(29, ""));
        yValues.add(new PieEntry(37, ""));

        PieDataSet pieDataSet = new PieDataSet(yValues, "Tasks");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(Color.RED, Color.BLUE);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);

        LineChart lineChart = view.findViewById(R.id.lineChart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);

        ArrayList<ILineDataSet> dataLists = new ArrayList<>();

        ArrayList<Entry> templst = new ArrayList<>();
        templst.add(new Entry(1, 5));
        templst.add(new Entry(2, 2));
        templst.add(new Entry(3, 0));
        templst.add(new Entry(4, 10));
        templst.add(new Entry(5, 1));
        templst.add(new Entry(6, 6));
        templst.add(new Entry(7, 5));
        templst.add(new Entry(8, 2));
        templst.add(new Entry(9, 0));
        templst.add(new Entry(10, 10));
        templst.add(new Entry(11, 1));
        templst.add(new Entry(12, 6));
        templst.add(new Entry(13, 5));
        templst.add(new Entry(14, 2));
        templst.add(new Entry(15, 0));
        templst.add(new Entry(16, 10));
        templst.add(new Entry(17, 1));
        templst.add(new Entry(18, 6));
        templst.add(new Entry(19, 5));
        templst.add(new Entry(20, 2));
        templst.add(new Entry(21, 0));
        templst.add(new Entry(22, 10));
        templst.add(new Entry(23, 1));
        templst.add(new Entry(24, 6));
        templst.add(new Entry(25, 5));
        templst.add(new Entry(26, 2));
        templst.add(new Entry(27, 0));
        templst.add(new Entry(28, 10));
        templst.add(new Entry(29, 1));
        templst.add(new Entry(30, 6));
        templst.add(new Entry(31, 8));

        LineDataSet lineDataSet = new LineDataSet(templst, "Выполненные");
        lineDataSet.setColors(Color.RED);
        lineDataSet.setLineWidth(2.5f);
        dataLists.add(lineDataSet);

        ArrayList<Entry> templst1 = new ArrayList<>();
        templst1.add(new Entry(1, 4));
        templst1.add(new Entry(2, 2));
        templst1.add(new Entry(3, 4));
        templst1.add(new Entry(4, 9));
        templst1.add(new Entry(5, 7));
        templst1.add(new Entry(6, 5));
        templst1.add(new Entry(7, 4));
        templst1.add(new Entry(8, 2));
        templst1.add(new Entry(9, 4));
        templst1.add(new Entry(10, 9));
        templst1.add(new Entry(11, 7));
        templst1.add(new Entry(12, 5));
        templst1.add(new Entry(13, 4));
        templst1.add(new Entry(14, 2));
        templst1.add(new Entry(15, 4));
        templst1.add(new Entry(16, 9));
        templst1.add(new Entry(17, 7));
        templst1.add(new Entry(18, 5));
        templst1.add(new Entry(19, 4));
        templst1.add(new Entry(20, 2));
        templst1.add(new Entry(21, 4));
        templst1.add(new Entry(22, 9));
        templst1.add(new Entry(23, 7));
        templst1.add(new Entry(24, 5));
        templst1.add(new Entry(25, 4));
        templst1.add(new Entry(26, 2));
        templst1.add(new Entry(27, 4));
        templst1.add(new Entry(28, 9));
        templst1.add(new Entry(29, 7));
        templst1.add(new Entry(30, 5));
        templst.add(new Entry(31, 6));

        LineDataSet lineDataSet1 = new LineDataSet(templst1, "Невыполненные");
        lineDataSet1.setLineWidth(2.5f);
        lineDataSet1.setColor(Color.BLUE);
        dataLists.add(lineDataSet1);

        LineData data = new LineData(dataLists);

        lineChart.setData(data);
        return view;
    }
}
