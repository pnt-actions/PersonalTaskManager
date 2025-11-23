package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Màn hình danh sách Task.
 * Sử dụng ViewModel + LiveData → UI tự cập nhật khi DB thay đổi.
 */
public class TaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private TaskAdapter adapter;
    private TaskViewModel viewModel;

    private static final int REQUEST_ADD_TASK = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_list);

        recyclerView = findViewById(R.id.rv_list_tasks);
        fabAdd = findViewById(R.id.fab_add_task);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(task -> {
            // Sau này thêm sửa task
        });
        recyclerView.setAdapter(adapter);

        // Lấy ViewModel
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Observe DB → UI tự cập nhật
        viewModel.getAllTasks().observe(this, tasks -> {
            adapter.setData(tasks);
        });

        // Nút mở màn thêm Task
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivityForResult(intent, REQUEST_ADD_TASK);
        });
    }

    // Không cần reload — LiveData tự làm
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
