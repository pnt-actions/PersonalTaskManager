package com.example.personaltaskmanager.features.task_manager.screens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách Task trong RecyclerView.
 * - Sử dụng ViewHolder pattern.
 * - Hỗ trợ click vào từng item để mở chi tiết.
 *
 * Note:
 *  Đây chỉ là adapter cho UI, không chứa logic load DB.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    // Danh sách task để hiển thị
    private List<Task> taskList = new ArrayList<>();

    // Listener để Activity xử lý khi người dùng click item
    private OnTaskClickListener listener;

    /**
     * Interface callback khi user nhấn vào 1 task.
     */
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    /**
     * Constructor — nhận listener từ Activity.
     */
    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    /**
     * Cập nhật dữ liệu cho RecyclerView.
     */
    public void setData(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate layout item task UI
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feature_task_manager_item_task, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        // Lấy task tương ứng
        Task task = taskList.get(position);

        // Gán dữ liệu cho item
        holder.tvTitle.setText(task.getTitle());
        holder.tvDesc.setText(task.getDescription());

        // Gọi listener khi user click item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTaskClick(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * ViewHolder lưu trữ view của từng item.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ view từ item layout
            tvTitle = itemView.findViewById(R.id.item_task_title);
            tvDesc  = itemView.findViewById(R.id.item_task_desc);
        }
    }
}
