package com.example.personaltaskmanager.features.task_manager.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personaltaskmanager.R;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_detail);

        // Ánh xạ view
        edtTitle = findViewById(R.id.edt_task_title);
        edtDescription = findViewById(R.id.edt_task_description);
        btnSave = findViewById(R.id.btn_save_task);

        // Xử lý lưu (tạm)
        btnSave.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();

            // TODO: Sau này gọi ViewModel để lưu DB
            // Hiện tại chỉ cần đóng Activity
            if (!title.isEmpty()) {
                finish();
            }
        });
    }
}
