package com.example.arraylist.items;

import java.util.ArrayList;

public class CompletedTasks {
    public int count;
    public ArrayList<Long> dates;
    public ArrayList<Integer> countPerDay;

    public CompletedTasks(ArrayList<Long> dates, ArrayList<Integer> countPerDay) {
        count = 0;
        this.dates = dates;
        for (int i = 0; i < dates.size(); i++)
            count += countPerDay.get(i);
        this.countPerDay = countPerDay;
    }
}
