package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách Task trong RecyclerView.
 * Giữ nguyên cấu trúc cũ, chỉ bổ sung toggle Completed.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList = new ArrayList<>();

    private OnTaskClickListener listener;
    private OnTaskDeleteListener deleteListener;
    private OnTaskToggleListener toggleListener;   // ⭐ NEW

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskDeleteListener {
        void onTaskDelete(Task task);
    }

    // ⭐ CALLBACK khi tick checkbox
    public interface OnTaskToggleListener {
        void onTaskToggle(Task task, boolean done);
    }

    public TaskAdapter(
            OnTaskClickListener listener,
            OnTaskDeleteListener deleteListener,
            OnTaskToggleListener toggleListener
    ) {
        this.listener = listener;
        this.deleteListener = deleteListener;
        this.toggleListener = toggleListener;
    }

    public void setData(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feature_task_manager_item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Gán text
        holder.textTitle.setText(task.getTitle());
        holder.textDeadline.setText(task.getDescription());

        // =============================
        // 1) NGĂN TRIGGER LISTENER LẶP LẠI
        // → Nếu không thì checkbox nhảy loạn khi scroll
        // =============================
        holder.checkboxTask.setOnCheckedChangeListener(null);
        holder.checkboxTask.setChecked(task.isCompleted());

        // =============================
        // 2) ÁP DỤNG STYLE (strike-through + alpha)
        // =============================
        applyCompletedStyle(holder, task.isCompleted());

        // =============================
        // 3) CLICK ITEM → mở Task Detail
        // =============================
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTaskClick(task);
        });

        // =============================
        // 4) CLICK DELETE
        // =============================
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onTaskDelete(task);
        });

        // =============================
        // 5) CHECKBOX TOGGLE
        // =============================
        holder.checkboxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (toggleListener != null) toggleListener.onTaskToggle(task, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * Áp dụng UI cho trạng thái completed:
     * - Gạch ngang title
     * - Giảm độ mờ text
     * - Đổi màu checkbox (xanh dương → xanh lá)
     */
    private void applyCompletedStyle(TaskViewHolder holder, boolean completed) {
        if (completed) {
            // Gạch ngang + mờ đi
            holder.textTitle.setPaintFlags(
                    holder.textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.textTitle.setAlpha(0.5f);
            holder.textDeadline.setAlpha(0.5f);

            // Checkbox tint xanh lá (task đã xong)
            int doneColor = holder.itemView.getResources()
                    .getColor(R.color.task_checkbox_done_tint);
            holder.checkboxTask.setButtonTintList(ColorStateList.valueOf(doneColor));
        } else {
            // Bỏ gạch ngang + full opacity
            holder.textTitle.setPaintFlags(
                    holder.textTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
            holder.textTitle.setAlpha(1f);
            holder.textDeadline.setAlpha(1f);

            // Checkbox tint xanh dương (task chưa xong)
            int normalColor = holder.itemView.getResources()
                    .getColor(R.color.task_checkbox_tint);
            holder.checkboxTask.setButtonTintList(ColorStateList.valueOf(normalColor));
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTask;
        TextView textTitle, textDeadline;
        CheckBox checkboxTask;
        ImageButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            imgTask = itemView.findViewById(R.id.imgTask);
            textTitle = itemView.findViewById(R.id.textTaskTitle);
            textDeadline = itemView.findViewById(R.id.textTaskDeadline);
            checkboxTask = itemView.findViewById(R.id.checkboxTask);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
