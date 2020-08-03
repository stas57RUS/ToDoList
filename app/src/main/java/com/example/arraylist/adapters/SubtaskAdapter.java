package com.example.arraylist.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arraylist.R;

import java.util.ArrayList;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.SubtasksViewHolder> {
    private ArrayList<String> elements;

    public SubtaskAdapter(ArrayList<String> elements) {
        this.elements = elements;
    }

    @NonNull
    @Override
    public SubtasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int LayoutIdForListItem = R.layout.subtask_edittext;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(LayoutIdForListItem, parent, false);
        return new SubtasksViewHolder(view, new CustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull SubtasksViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    class SubtasksViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        ImageButton imageButton;
        CustomEditTextListener customEditTextListener;

        public SubtasksViewHolder(@NonNull View itemView, CustomEditTextListener customEditTextListener) {
            super(itemView);
            this.customEditTextListener = customEditTextListener;
            editText = itemView.findViewById(R.id.editText);
            imageButton = itemView.findViewById(R.id.imageButton);
            editText.addTextChangedListener(customEditTextListener);
        }

        void bind(final int position) {
            if (elements.size() > 1)
                editText.setHint("Cледующий шаг");
            else
                editText.setHint("Добавьте шаг");

            if (elements.get(position).equals(""))
                imageButton.setEnabled(false);
            else
                imageButton.setEnabled(true);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    elements.remove(position);
                    notifyDataSetChanged();
                }
            });

            customEditTextListener.updatePosition(getAdapterPosition());
            editText.setText(elements.get(position));
            editText.setSelection(editText.getText().length());
        }
    }

    class CustomEditTextListener implements TextWatcher {

        int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            elements.set(position, s.toString());
            if (!elements.get(elements.size() - 1).equals("")) {
                elements.add("");
                updatePosition(position);
                SubtaskAdapter.this.notifyItemChanged(position);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public ArrayList<String> getElements() {
        return elements;
    }
}
