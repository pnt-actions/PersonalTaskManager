package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_list);

        // Ánh xạ view
        recyclerView = findViewById(R.id.rv_list_tasks);
        fabAdd = findViewById(R.id.fab_add_task);

        // Tạo RecyclerView (giai đoạn này chưa có Adapter thật)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Mở màn chi tiết task (tạm thời là màn tạo task)
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
            startActivity(intent);
        });
    }
}
