package com.example.personaltaskmanager.features.task_manager.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;

/**
 * Màn hình thêm / sửa Task.
 * Hiện tại chỉ làm chức năng thêm mới.
 * Sử dụng đúng kiến trúc MVVM → gọi ViewModel để lưu DB.
 */
public class TaskDetailActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Button btnSave;

    // DÙNG VIEWMODEL — KHÔNG DÙNG REPOSITORY TRỰC TIẾP
    private TaskViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_detail);

        edtTitle = findViewById(R.id.edt_task_title);
        edtDescription = findViewById(R.id.edt_task_description);
        btnSave = findViewById(R.id.btn_save_task);

        // KHỞI TẠO VIEWMODEL
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        btnSave.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();

            if (title.isEmpty()) {
                edtTitle.setError("Tên công việc không được để trống");
                return;
            }

            // GỌI VIEWMODEL → LiveData ở TaskListActivity sẽ tự UPDATE
            viewModel.addTask(title, desc);

            setResult(RESULT_OK);
            finish();
        });
    }
}
