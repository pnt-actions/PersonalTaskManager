package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personaltaskmanager.R;

public class TaskManagerMainActivity extends AppCompatActivity {

    private LinearLayout cardOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_main);

        cardOverview = findViewById(R.id.card_search);

        cardOverview.setOnClickListener(v -> {
            Intent intent = new Intent(TaskManagerMainActivity.this, TaskListActivity.class);
            startActivity(intent);
        });
    }
}
