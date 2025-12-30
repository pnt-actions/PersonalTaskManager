package com.example.personaltaskmanager.features.task_manager.screens.workspace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlock;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlockParser;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog để chọn task đích khi di chuyển block.
 */
public class MoveBlockDialog extends DialogFragment {

    public interface OnTaskSelectedListener {
        void onTaskSelected(int targetTaskId);
    }

    private NotionBlock block;
    private int currentTaskId;
    private TaskViewModel viewModel;
    private OnTaskSelectedListener listener;
    private List<Task> allTasks = new ArrayList<>();

    public MoveBlockDialog(NotionBlock block, int currentTaskId, TaskViewModel viewModel, OnTaskSelectedListener listener) {
        this.block = block;
        this.currentTaskId = currentTaskId;
        this.viewModel = viewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn task đích");

        ListView listView = new ListView(requireContext());
        TaskListAdapter adapter = new TaskListAdapter(requireContext(), allTasks);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position < allTasks.size()) {
                Task selectedTask = allTasks.get(position);
                if (selectedTask.getId() == currentTaskId) {
                    Toast.makeText(requireContext(), "Không thể di chuyển vào task hiện tại", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null) {
                    listener.onTaskSelected(selectedTask.getId());
                }
                dismiss();
            }
        });

        builder.setView(listView);
        builder.setNegativeButton("Hủy", null);

        // Load tasks - Kotlin property allTasks tự động có getter getAllTasks()
        viewModel.getAllTasks().observe(this, tasks -> {
            allTasks.clear();
            if (tasks != null) {
                for (Task task : tasks) {
                    if (task.getId() != currentTaskId) {
                        allTasks.add(task);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        });

        return builder.create();
    }

    private static class TaskListAdapter extends ArrayAdapter<Task> {
        public TaskListAdapter(Context context, List<Task> tasks) {
            super(context, android.R.layout.simple_list_item_1, tasks);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView textView = (TextView) convertView;
            Task task = getItem(position);
            if (task != null) {
                textView.setText(task.getTitle());
            }
            return convertView;
        }
    }
}

