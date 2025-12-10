package com.example.datttph39843_ass.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datttph39843_ass.DTO.Task;
import com.example.datttph39843_ass.R;

import java.util.Objects;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onItemClick(Task task);
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }

    public TaskAdapter(OnItemActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    Objects.equals(oldItem.getContent(), newItem.getContent()) &&
                    oldItem.getStatus() == newItem.getStatus() &&
                    Objects.equals(oldItem.getStartDate(), newItem.getStartDate()) &&
                    Objects.equals(oldItem.getEndDate(), newItem.getEndDate());
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTaskName, tvTaskContent, tvTaskStatus, tvTaskDates;
        private final Button btnEditTask, btnDeleteTask;
        private final OnItemActionListener listener;
        private final Context context;

        public TaskViewHolder(@NonNull View itemView, OnItemActionListener listener) {
            super(itemView);
            this.listener = listener;
            this.context = itemView.getContext();
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskContent = itemView.findViewById(R.id.tvTaskContent);
            tvTaskStatus = itemView.findViewById(R.id.tvTaskStatus);
            tvTaskDates = itemView.findViewById(R.id.tvTaskDates);
            btnEditTask = itemView.findViewById(R.id.btnEditTask);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }

        public void bind(final Task task) {
            tvTaskName.setText(task.getName());
            tvTaskContent.setText(task.getContent());
            tvTaskDates.setText("Từ: " + task.getStartDate() + " - Đến: " + (task.getEndDate() != null && !task.getEndDate().isEmpty() ? task.getEndDate() : "N/A"));
            tvTaskStatus.setText("Trạng thái: " + getStatusText(task.getStatus()));
            updateTextStyle(task.getStatus() == 2);

            btnEditTask.setOnClickListener(v -> listener.onEditClick(task));
            btnDeleteTask.setOnClickListener(v -> listener.onDeleteClick(task));
            
            // Allow quick status change only if the task is NOT completed
            itemView.setOnClickListener(v -> {
                if (task.getStatus() != 2) {
                    listener.onItemClick(task);
                }
            });
        }

        private void updateTextStyle(boolean isDone) {
            if (isDone) {
                tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTaskName.setTextColor(ContextCompat.getColor(context, R.color.gray_50));
            } else {
                tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTaskName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
        }

        private String getStatusText(int status) {
            switch (status) {
                case 0: return "Chưa làm";
                case 1: return "Đang làm";
                case 2: return "Hoàn thành";
                default: return "Không xác định";
            }
        }
    }
}
