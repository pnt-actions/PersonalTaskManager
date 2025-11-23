package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Màn hình chính của module Task Manager.
 * Gồm:
 *  - Thanh tìm kiếm
 *  - Card tổng quan
 *  - Danh sách task
 *  - Bottom navigation
 *  - Nút thêm (+)
 */
public class TaskManagerMainActivity extends AppCompatActivity {

    private LinearLayout navHome, navTasks, navProfile;
    private FloatingActionButton fabAddTask;

    // ==== PHẦN BỔ SUNG ====
    private RecyclerView rvTasks;
    private TaskAdapter adapter;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_main);

        initViews();
        setupBottomNav();
        setupActions();
        setupRecyclerView();       // <--- PHẦN BỔ SUNG
        setupViewModelObserve();   // <--- PHẦN BỔ SUNG
    }

    private void initViews() {
        LinearLayout bottomNav = findViewById(R.id.bottom_nav);

        navHome = bottomNav.findViewById(R.id.nav_home);
        navTasks = bottomNav.findViewById(R.id.nav_tasks);
        navProfile = bottomNav.findViewById(R.id.nav_profile);

        fabAddTask = findViewById(R.id.fab_add_task);

        // ==== PHẦN BỔ SUNG ====
        rvTasks = findViewById(R.id.rv_tasks);
    }

    private void setupBottomNav() {

        navHome.setOnClickListener(v -> {
            // TODO
        });

        navTasks.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskListActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            // TODO
        });
    }

    private void setupActions() {

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_add_small).setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivity(intent);
        });
    }

    // ================================
    //      PHẦN BỔ SUNG — HIỂN THỊ TASK
    // ================================

    private void setupRecyclerView() {
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(task -> {
            Intent intent = new Intent(TaskManagerMainActivity.this, TaskDetailActivity.class);
            intent.putExtra("task_id", task.getId());
            startActivity(intent);
        });
        rvTasks.setAdapter(adapter);
    }

    private void setupViewModelObserve() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        taskViewModel.getAllTasks().observe(this, tasks -> {
            adapter.setData(tasks);   // <--- HIỂN THỊ TASK TRÊN MÀN HÌNH CHÍNH
        });
    }
}
