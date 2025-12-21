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

    fun getTaskByUuid(uuid: String): LiveData<Task> {
        return repo.getTaskByUuid(uuid)
    }

    fun toggleCompleted(task: Task, done: Boolean) {
        task.isCompleted = done
        repo.updateTask(task)
    }

    // ===== FILTER & SEARCH =====
    fun getTasksByPriority(priority: String): LiveData<List<Task>> {
        return repo.getTasksByPriority(priority)
    }

    fun getSubtasks(parentTaskId: Int): LiveData<List<Task>> {
        return repo.getSubtasks(parentTaskId)
    }

    fun searchTasks(query: String): LiveData<List<Task>> {
        return repo.searchTasks(query)
    }

    fun getTasksByTag(tag: String): LiveData<List<Task>> {
        return repo.getTasksByTag(tag)
    }

    fun getTasksFiltered(priority: String?, isCompleted: Boolean?, tag: String?, 
                         startDate: Long, endDate: Long, sortBy: String): LiveData<List<Task>> {
        return repo.getTasksFiltered(priority, isCompleted, tag, startDate, endDate, sortBy)
    }

    // ===== STATISTICS =====
    fun getCompletedTasksCount(): LiveData<Int> {
        return repo.getCompletedTasksCount()
    }

    fun getPendingTasksCount(): LiveData<Int> {
        return repo.getPendingTasksCount()
    }

    fun getOverdueTasksCount(): LiveData<Int> {
        return repo.getOverdueTasksCount()
    }

    fun getCompletedTasksCountByDate(startDate: Long, endDate: Long): LiveData<Int> {
        return repo.getCompletedTasksCountByDate(startDate, endDate)
    }
}
