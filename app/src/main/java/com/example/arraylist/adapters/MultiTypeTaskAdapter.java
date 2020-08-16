package com.example.arraylist.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.arraylist.DB.DBHelper;
import com.example.arraylist.items.Subtask;
import com.example.arraylist.items.Task;
import com.example.arraylist.R;
import com.example.arraylist.activities.AddActivity;
import com.example.arraylist.other.setZeroTimeDate;
import com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MultiTypeTaskAdapter extends MultiTypeExpandableRecyclerViewAdapter<MultiTypeTaskAdapter.TaskViewHolder,
        MultiTypeTaskAdapter.SubtaskViewHolder> {

    private List<? extends  ExpandableGroup> groups;
    private int parent;
    private Context context;

    public static final int TYPE_IS_COMMENT_IS_SUBTASKS = 3;
    public static final int TYPE_IS_COMMENT_ISNT_SUBTASKS = 4;
    public static final int TYPE_ISNT_COMMENT_ISNT_SUBTASKS= 5;
    public static final int TYPE_ISNT_COMMENT_IS_SUBTASKS = 6;
    public static final int TYPE_ISNT_CHECKBOX_IS_COMMENT_IS_SUBTASKS = 7;
    public static final int TYPE_ISNT_CHECKBOX_IS_COMMENT_ISNT_SUBTASKS = 8;
    public static final int TYPE_ISNT_CHECKBOX_ISNT_COMMENT_IS_SUBTASKS = 9;
    public static final int TYPE_ISNT_CHECKBOX_ISNT_COMMENT_ISNT_SUBTASKS = 10;

    public static final int PARENT_HOME = 13;
    public static final int PARENT_COMPLETED = 14;
    public static final int PARENT_FAILED = 15;
    public static final int PARENT_PLANNED = 16;

    public MultiTypeTaskAdapter(List<? extends ExpandableGroup> groups, int parent, Context context) {
        super(groups);
        this.groups = groups;
        this.parent = parent;
        this.context = context;
    }

    class TaskViewHolder extends GroupViewHolder {

        private int viewType;
        private CheckBox checkBox;
        private TextView tvTask, date;
        private Button buttonComment;
        private ImageButton imageButton;
        private ImageView arrow;

        public TaskViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            tvTask = itemView.findViewById(R.id.task);
            date = itemView.findViewById(R.id.date);
            imageButton = itemView.findViewById(R.id.fab);

            switch (viewType){
                case TYPE_IS_COMMENT_IS_SUBTASKS:
                    checkBox = itemView.findViewById(R.id.checkBox);
                    arrow = itemView.findViewById(R.id.arrow);
                    buttonComment = itemView.findViewById(R.id.button);
                    break;
                case TYPE_IS_COMMENT_ISNT_SUBTASKS:
                    checkBox = itemView.findViewById(R.id.checkBox);
                    buttonComment = itemView.findViewById(R.id.button);
                case TYPE_ISNT_CHECKBOX_IS_COMMENT_ISNT_SUBTASKS:
                    buttonComment = itemView.findViewById(R.id.button);
                    break;
                case TYPE_ISNT_COMMENT_ISNT_SUBTASKS:
                    checkBox = itemView.findViewById(R.id.checkBox);
                    break;
                case TYPE_ISNT_COMMENT_IS_SUBTASKS:
                    checkBox = itemView.findViewById(R.id.checkBox);
                    arrow = itemView.findViewById(R.id.arrow);
                    break;
                case TYPE_ISNT_CHECKBOX_ISNT_COMMENT_IS_SUBTASKS:
                    arrow = itemView.findViewById(R.id.arrow);
                    break;
                case TYPE_ISNT_CHECKBOX_IS_COMMENT_IS_SUBTASKS:
                    buttonComment = itemView.findViewById(R.id.button);
                    arrow = itemView.findViewById(R.id.arrow);
                    break;
            }
        }

        public void bind(final Task task, int viewType, final int parent) {
            tvTask.setText(task.task);
            date.setText(task.dateString);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (parent){
                        case MultiTypeTaskAdapter.PARENT_HOME:
                            deleteTask(v, PARENT_HOME);
                            break;
                        case MultiTypeTaskAdapter.PARENT_COMPLETED:
                            deleteTask(v, PARENT_COMPLETED);
                            break;
                        case MultiTypeTaskAdapter.PARENT_FAILED:
                            deleteTask(v, PARENT_FAILED);
                            break;
                        case MultiTypeTaskAdapter.PARENT_PLANNED:
                            showPopup(v, PARENT_PLANNED);
                            break;
                    }
                }

                public void showPopup(final View v, final int parent) {
                    PopupMenu popup = new PopupMenu(v.getContext(), imageButton);
                    popup.getMenuInflater().inflate(R.menu.menu_for_tasks, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.edit:
                                    Intent intent = new Intent(v.getContext(), AddActivity.class);
                                    intent.putExtra("type", AddActivity.TYPE_EDIT);
                                    intent.putExtra("task_id", task.id);
                                    context.startActivity(intent);
                                    break;
                                case R.id.delete:
                                    deleteTask(v, parent);
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }

                public void deleteTask(View v, int parent) {
                    ArrayList<Long> ids = new ArrayList<>();
                    for (int i = 0; i < task.subtasks.size(); i++)
                        ids.add(task.subtasks.get(i).id);

                    DBHelper dbHelper = new DBHelper(v.getContext());
                    switch (parent) {
                        case PARENT_HOME:
                            dbHelper.deleteHomeTask(task.id);
                            break;
                        case PARENT_COMPLETED:
                            dbHelper.deleteCompleteTask(task.id);
                            break;
                        case PARENT_FAILED:
                            dbHelper.deleteFaildTask(task.id);
                            break;
                        case PARENT_PLANNED:
                            dbHelper.deletePlannedTask(task.id);
                            break;
                    }
                    dbHelper.deleteSubtasks(ids);
                    remove(getAdapterPosition());
                }
            });

            if (viewType >= 3 && viewType <= 6){
                checkBox.setChecked(false);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()){
                            DBHelper dbHelper = new DBHelper(v.getContext());
                            dbHelper.addTableComplete(task);
                            dbHelper.deleteHomeTask(task.id);
                            remove(getAdapterPosition());
                            dbHelper.addNewStats(new setZeroTimeDate().transform(new Date()).getTime(),
                                    DBHelper.STATS_TYPE_COMPLETED);
                        }
                    }
                });
            }

            if (viewType == MultiTypeTaskAdapter.TYPE_IS_COMMENT_IS_SUBTASKS ||
                    viewType == MultiTypeTaskAdapter.TYPE_IS_COMMENT_ISNT_SUBTASKS ||
                    viewType == MultiTypeTaskAdapter.TYPE_ISNT_CHECKBOX_IS_COMMENT_IS_SUBTASKS ||
                    viewType == MultiTypeTaskAdapter.TYPE_ISNT_CHECKBOX_IS_COMMENT_ISNT_SUBTASKS) {
                buttonComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog dialog = new AlertDialog.Builder(v.getContext()).
                                setMessage(task.comment)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    }
                });
            }
        }

        public void remove(int position){
            groups.remove(position);
            notifyDataSetChanged();
        }
    }

    class SubtaskViewHolder extends ChildViewHolder {

        private DBHelper dbHelper;
        private TextView task;
        private CheckBox checkBox;

        public SubtaskViewHolder(View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.subtask);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(final Subtask subtask) {
            task.setText(subtask.task);
            dbHelper = new DBHelper(itemView.getContext());
            final Boolean checked = dbHelper.getSubtaskChecked(subtask.id);
            checkBox.setChecked(checked);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper = new DBHelper(v.getContext());
                    dbHelper.updateTableSubtask_checkBox(subtask.id, checked);
                }
            });
        }
    }

    @Override
    public int getGroupViewType(int position, ExpandableGroup group) {
        Task task = (Task) group;
        if (parent == PARENT_HOME) { // Если активный элемент
            if (task.comment.length() > 0) { // Если есть коммент
                if (task.subtasks.size() > 0) {  // Если есть пд.
                    return TYPE_IS_COMMENT_IS_SUBTASKS;
                }
                else { // Нет пд.
                    return TYPE_IS_COMMENT_ISNT_SUBTASKS;
                }
            }
            else { // Нет комента
                if (task.subtasks.size() > 0) {  // Если есть пд.
                    return TYPE_ISNT_COMMENT_IS_SUBTASKS;
                }
                else { // Нет пд.
                    return TYPE_ISNT_COMMENT_ISNT_SUBTASKS;
                }
            }
        }
        else { // Все кроме активных элементов (нет CheckBox)
            if (task.comment.length() > 0) { // Если есть коммент
                if (task.subtasks.size() > 0) {  // Если есть пд.
                    return TYPE_ISNT_CHECKBOX_IS_COMMENT_IS_SUBTASKS;
                }
                else { // Нет пд.
                    return TYPE_ISNT_CHECKBOX_IS_COMMENT_ISNT_SUBTASKS;
                }
            }
            else { // Нет комента
                if (task.subtasks.size() > 0) {  // Если есть пд.
                    return TYPE_ISNT_CHECKBOX_ISNT_COMMENT_IS_SUBTASKS;
                }
                else { // Нет пд.
                    return TYPE_ISNT_CHECKBOX_ISNT_COMMENT_ISNT_SUBTASKS;
                }
            }
        }
    }

    @Override
    public boolean isGroup(int viewType) {
        return viewType == TYPE_IS_COMMENT_IS_SUBTASKS ||
                viewType == TYPE_IS_COMMENT_ISNT_SUBTASKS ||
                viewType == TYPE_ISNT_COMMENT_IS_SUBTASKS ||
                viewType == TYPE_ISNT_COMMENT_ISNT_SUBTASKS ||
                viewType == TYPE_ISNT_CHECKBOX_IS_COMMENT_IS_SUBTASKS ||
                viewType == TYPE_ISNT_CHECKBOX_IS_COMMENT_ISNT_SUBTASKS ||
                viewType == TYPE_ISNT_CHECKBOX_ISNT_COMMENT_IS_SUBTASKS ||
                viewType == TYPE_ISNT_CHECKBOX_ISNT_COMMENT_ISNT_SUBTASKS;
    }

    @Override
    public TaskViewHolder onCreateGroupViewHolder(ViewGroup parentView, int viewType) {
        int LayoutIdForListItem = 0;

        switch (viewType){
            case TYPE_IS_COMMENT_IS_SUBTASKS:
                LayoutIdForListItem = R.layout.is_comment_is_subtasks;
                break;
            case TYPE_IS_COMMENT_ISNT_SUBTASKS:
                LayoutIdForListItem = R.layout.is_comment_isnt_subtasks;
                break;
            case TYPE_ISNT_COMMENT_ISNT_SUBTASKS:
                LayoutIdForListItem = R.layout.isnt_comment_isnt_subtasks;
                break;
            case TYPE_ISNT_COMMENT_IS_SUBTASKS:
                LayoutIdForListItem = R.layout.isnt_comment_is_subtasks;
                break;
            case TYPE_ISNT_CHECKBOX_IS_COMMENT_ISNT_SUBTASKS:
                LayoutIdForListItem = R.layout.isnt_checkbox_is_comment_isnt_subtasks;
                break;
            case TYPE_ISNT_CHECKBOX_IS_COMMENT_IS_SUBTASKS:
                LayoutIdForListItem = R.layout.isnt_checkbox_is_comment_is_subtasks;
                break;
            case TYPE_ISNT_CHECKBOX_ISNT_COMMENT_IS_SUBTASKS:
                LayoutIdForListItem = R.layout.isnt_checkbox_isnt_comment_is_subtasks;
                break;
            case TYPE_ISNT_CHECKBOX_ISNT_COMMENT_ISNT_SUBTASKS:
                LayoutIdForListItem = R.layout.isnt_checkbox_isnt_comment_isnt_subtasks;
                break;
        }

        LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
        View view = inflater.inflate(LayoutIdForListItem, parentView, false);

        if (parent == PARENT_PLANNED) {
            ImageButton imageButton = view.findViewById(R.id.fab);
            imageButton.setImageResource(R.drawable.pending_24px);
        }
        return new TaskViewHolder(view, viewType);
    }

    @Override
    public void onBindGroupViewHolder(TaskViewHolder holder, int flatPosition, ExpandableGroup group) {
        int viewType = getGroupViewType(flatPosition, group);
        final Task task = (Task) group;
        holder.bind(task, viewType, parent);
    }

    @Override
    public SubtaskViewHolder onCreateChildViewHolder(ViewGroup parentView, int viewType) {
        CheckBox checkBox;
        LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
        View view = inflater.inflate(R.layout.subtask, parentView, false);
        if (parent == PARENT_HOME) {
            checkBox = view.findViewById(R.id.checkBox);
            checkBox.setEnabled(true);
        }
        return new SubtaskViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(SubtaskViewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {
        final Subtask subtask = (Subtask) group.getItems().get(childIndex);
        holder.bind(subtask);
    }
}
