package com.example.personaltaskmanager.features.task_manager.screens;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.utils.DateUtils;
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel;

import java.util.Calendar;

/**
 * Màn hình thêm / sửa Task.
 * Giữ nguyên code cũ, chỉ bổ sung deadline + DatePicker + chọn ảnh công việc.
 */
public class TaskDetailActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription, edtDate;
    private Button btnSave;
    private ImageButton btnBack;

    private ImageView imgTask;
    private TextView btnPickImage;

    private TaskViewModel viewModel;

    private int taskId = -1;
    private Task currentTask = null;

    private long selectedDeadline = System.currentTimeMillis();
    private String selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_task_manager_detail);

        setLightStatusBar();
        initViews();

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        loadTaskIfEditMode();
        setupListeners();
    }

    private void initViews() {
        edtTitle = findViewById(R.id.edt_task_title);
        edtDescription = findViewById(R.id.edt_task_description);
        edtDate = findViewById(R.id.edt_task_date);
        btnSave = findViewById(R.id.btn_save_task);
        btnBack = findViewById(R.id.btn_back);

        imgTask = findViewById(R.id.img_task);
        btnPickImage = findViewById(R.id.btn_pick_image);
    }

    /** Load dữ liệu nếu đang sửa task */
    private void loadTaskIfEditMode() {
        taskId = getIntent().getIntExtra("task_id", -1);

        if (taskId != -1) {
            viewModel.getTaskById(taskId).observe(this, task -> {
                if (task == null) return;

                currentTask = task;

                edtTitle.setText(task.getTitle());
                edtDescription.setText(task.getDescription());

                selectedDeadline = task.getDeadline();
                edtDate.setText(DateUtils.formatDate(selectedDeadline));

                if (task.getImageUri() != null) {
                    selectedImageUri = task.getImageUri();
                    imgTask.setImageURI(Uri.parse(task.getImageUri()));
                }

                btnSave.setText("Cập nhật công việc");
            });
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        edtDate.setOnClickListener(v -> openDatePicker());
        btnSave.setOnClickListener(v -> saveTask());

        btnPickImage.setOnClickListener(v -> openGallery());
        imgTask.setOnClickListener(v -> openGallery());
    }

    /**
     * Mở Gallery chọn ảnh
     * Dùng ACTION_OPEN_DOCUMENT để đảm bảo persist permission
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );
        pickImageLauncher.launch(intent);
    }

    /**
     * Nhận kết quả chọn ảnh + persist quyền đọc URI
     */
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                final int takeFlags =
                                        result.getData().getFlags()
                                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                                getContentResolver()
                                        .takePersistableUriPermission(uri, takeFlags);

                                selectedImageUri = uri.toString();
                                imgTask.setImageURI(uri);
                            }
                        }
                    }
            );

    private void openDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDeadline);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                R.style.TaskManagerDatePickerTheme,
                (view, year, month, day) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day, 0, 0, 0);
                    selectedDeadline = c.getTimeInMillis();
                    edtDate.setText(DateUtils.formatDate(selectedDeadline));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveTask() {
        String title = edtTitle.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError("Tên công việc không được để trống");
            return;
        }

        // UPDATE
        if (currentTask != null) {
            currentTask.setImageUri(selectedImageUri);
            viewModel.updateTask(currentTask, title, desc, selectedDeadline);
            setResult(RESULT_OK);
            finish();
            return;
        }

        // ADD
        viewModel.addTask(title, desc, selectedDeadline, selectedImageUri);
        setResult(RESULT_OK);
        finish();
    }

    /** Light status bar */
    private void setLightStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.WHITE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        } else {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
    }
}
