package com.example.personaltaskmanager.features.task_manager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository;
import com.example.personaltaskmanager.features.task_manager.domain.usecase.AddTaskUseCase;
import com.example.personaltaskmanager.features.task_manager.domain.usecase.GetTasksUseCase;

import java.util.List;

/**
 * TaskViewModel quản lý dữ liệu Task theo mô hình MVVM.
 * - Lấy dữ liệu từ DB qua Repository + UseCase
 * - Expose LiveData để Activity observe (UI tự cập nhật)
 */
public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final GetTasksUseCase getTasksUseCase;
    private final AddTaskUseCase addTaskUseCase;

    // LiveData UI quan sát
    private LiveData<List<Task>> allTasksLiveData;

    public TaskViewModel(@NonNull Application application) {
        super(application);

        repository = new TaskRepository(application);
        getTasksUseCase = new GetTasksUseCase(repository);
        addTaskUseCase = new AddTaskUseCase(repository);

        // Nhận LiveData từ UseCase
        allTasksLiveData = getTasksUseCase.execute();
    }

    /**
     * Getter cho Activity observe
     */
    public LiveData<List<Task>> getAllTasks() {
        return allTasksLiveData;
    }

    /**
     * Thêm task mới vào DB
     */
    public void addTask(String title, String description) {
        Task task = new Task(title, description, System.currentTimeMillis());
        addTaskUseCase.execute(task);
        // KHÔNG CẦN load lại danh sách — LiveData tự update
    }
}
