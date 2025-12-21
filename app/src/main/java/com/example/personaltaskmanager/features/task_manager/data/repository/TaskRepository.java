package com.example.personaltaskmanager.features.task_manager.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.task_manager.data.local.dao.TaskDao;
import com.example.personaltaskmanager.features.task_manager.data.local.db.AppDatabase;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;

import java.util.List;

/**
 * Repository trung gian giữa DB và ViewModel.
 * Giữ nguyên logic cũ, chỉ bổ sung getTaskById().
 */
public class TaskRepository {

    private final TaskDao dao;
    private final AuthRepository authRepo;

    public TaskRepository(Context context) {
        dao = AppDatabase.getInstance(context).taskDao();
        authRepo = new AuthRepository(context);
    }

    private int getCurrentUserId() {
        User u = authRepo.getCurrentUser();
        return (u != null) ? u.id : -1;
    }

    public LiveData<List<Task>> getAllTasks() {
        return dao.getAllTasks(getCurrentUserId());
    }

    public void addTask(Task task) {
        task.setUserId(getCurrentUserId());
        // Đảm bảo UUID luôn có (nếu chưa có thì generate)
        if (task.getUuid() == null || task.getUuid().isEmpty()) {
            task.setUuid(java.util.UUID.randomUUID().toString());
        }
        AppDatabase.databaseWriteExecutor.execute(() -> dao.insertTask(task));
    }

    public void updateTask(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Đảm bảo UUID luôn có (nếu chưa có thì generate)
            if (task.getUuid() == null || task.getUuid().isEmpty()) {
                task.setUuid(java.util.UUID.randomUUID().toString());
            }
            dao.updateTask(task);
        });
    }

    public void deleteTask(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> dao.deleteTask(task));
    }

    public LiveData<List<Task>> getTasksByDate(long start, long end) {
        return dao.getTasksByDate(getCurrentUserId(), start, end);
    }

    // SỬA: trả LiveData
    public LiveData<Task> getTaskById(int taskId) {
        return dao.getTaskById(taskId);
    }

    // Lấy task đồng bộ (chạy trong background thread)
    public Task getTaskByIdSync(int taskId) {
        return dao.getTaskByIdSync(taskId);
    }

    public LiveData<Task> getTaskByUuid(String uuid) {
        return dao.getTaskByUuid(uuid);
    }

    public Task getTaskByUuidSync(String uuid) {
        return dao.getTaskByUuidSync(uuid);
    }
}
