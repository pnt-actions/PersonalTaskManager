package com.example.personaltaskmanager.features.task_manager.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.task_manager.data.local.dao.TaskDao;
import com.example.personaltaskmanager.features.task_manager.data.local.db.AppDatabase;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository quản lý truy cập DB.
 * Dùng Executor để tránh lỗi chạy DB trên main thread.
 */
public class TaskRepository {

    private final TaskDao taskDao;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TaskRepository(Context context) {
        this.taskDao = AppDatabase.getInstance(context).taskDao();
    }

    // Lấy danh sách Task dạng LiveData → TaskListActivity tự động update UI
    public LiveData<List<Task>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    // INSERT
    public long addTask(Task task) {
        try {
            return executor.submit(() -> taskDao.insertTask(task)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // UPDATE
    public void updateTask(Task task) {
        executor.execute(() -> taskDao.updateTask(task));
    }

    // DELETE
    public void deleteTask(Task task) {
        executor.execute(() -> taskDao.deleteTask(task));
    }
}
