package com.example.personaltaskmanager.features.task_manager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.personaltaskmanager.features.task_manager.data.model.Task
import com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository

/**
 * ViewModel cho Task.
 * Giữ nguyên code cũ, chỉ bổ sung các hàm cần thiết cho UI.
 */
class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = TaskRepository(app)

    val allTasks: LiveData<List<Task>> = repo.getAllTasks()

    /** ADD TASK (không ảnh – logic cũ) */
    fun addTask(title: String, desc: String, deadline: Long) {
        val t = Task(
            title,
            desc,
            System.currentTimeMillis(),
            deadline,
            "",
            "",
            0
        )
        repo.addTask(t)
    }

    /** ADD TASK (có ảnh công việc) */
    fun addTask(title: String, desc: String, deadline: Long, imageUri: String?) {
        val t = Task(
            title,
            desc,
            System.currentTimeMillis(),
            deadline,
            "",
            "",
            0
        )
        t.imageUri = imageUri
        repo.addTask(t)
    }

    fun updateTask(task: Task) {
        repo.updateTask(task)
    }

    fun updateTask(task: Task, title: String, desc: String, deadline: Long) {
        task.title = title
        task.description = desc
        task.deadline = deadline
        repo.updateTask(task)
    }

    fun deleteTask(task: Task) {
        repo.deleteTask(task)
    }

    fun getTasksByDate(start: Long, end: Long): LiveData<List<Task>> {
        return repo.getTasksByDate(start, end)
    }

    // SỬA: trả LiveData<Task>
    fun getTaskById(taskId: Int): LiveData<Task> {
        return repo.getTaskById(taskId)
    }

    fun toggleCompleted(task: Task, done: Boolean) {
        task.isCompleted = done
        repo.updateTask(task)
    }
}
