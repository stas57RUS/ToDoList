package com.example.arraylist.items;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;

public class Task extends ExpandableGroup {

    public Long id;
    public String task;
    public String comment;
    public String dateString;
    public Long dateStart, dateFinish;
    public ArrayList<Subtask> subtasks;

    public Task(Long id, String task, String comment, String dateString,
                Long dateStart, Long dateFinish, ArrayList<Subtask> subtasks) {
        super(task, subtasks);
        this.id = id;
        this.task = task;
        this.comment = comment;
        this.dateString = dateString;
        this.dateStart = dateStart;
        this.dateFinish = dateFinish;
        this.subtasks = subtasks;
    }
}
