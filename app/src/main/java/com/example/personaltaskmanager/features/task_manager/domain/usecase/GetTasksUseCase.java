package com.example.personaltaskmanager.features.task_manager.domain.usecase;

import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository;

import java.util.List;

public class GetTasksUseCase {

    private final TaskRepository repository;

    public GetTasksUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    // Trả về LiveData để UI tự động update
    public LiveData<List<Task>> execute() {
        return repository.getAllTasks();
    }
}
