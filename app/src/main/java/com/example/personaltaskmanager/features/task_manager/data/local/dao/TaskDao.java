package com.example.personaltaskmanager.features.task_manager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personaltaskmanager.features.task_manager.data.model.Task;

import java.util.List;

/**
 * DAO xử lý CRUD với Room DB.
 * Giữ nguyên toàn bộ code cũ, chỉ bổ sung userId vào query.
 */
@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    LiveData<List<Task>> getAllTasks(int userId);

    // SỬA: trả về LiveData để tránh query main thread
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    LiveData<Task> getTaskById(int taskId);

    // Lấy task đồng bộ (dùng trong background thread)
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    Task getTaskByIdSync(int taskId);

    // Lấy task theo UUID (đảm bảo chính xác, không nhầm lẫn)
    @Query("SELECT * FROM tasks WHERE uuid = :uuid LIMIT 1")
    LiveData<Task> getTaskByUuid(String uuid);

    // Lấy task theo UUID đồng bộ
    @Query("SELECT * FROM tasks WHERE uuid = :uuid LIMIT 1")
    Task getTaskByUuidSync(String uuid);

    @Insert
    long insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("UPDATE tasks SET isCompleted = :done WHERE id = :taskId")
    void updateCompleted(int taskId, boolean done);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND deadline BETWEEN :start AND :end ORDER BY deadline ASC")
    LiveData<List<Task>> getTasksByDate(int userId, long start, long end);

    // ===== FILTER BY PRIORITY =====
    @Query("SELECT * FROM tasks WHERE userId = :userId AND priority = :priority ORDER BY id DESC")
    LiveData<List<Task>> getTasksByPriority(int userId, String priority);

    // ===== FILTER BY PARENT TASK (SUBTASKS) =====
    @Query("SELECT * FROM tasks WHERE userId = :userId AND parentTaskId = :parentTaskId ORDER BY id DESC")
    LiveData<List<Task>> getSubtasks(int userId, int parentTaskId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND parentTaskId = :parentTaskId ORDER BY id DESC")
    List<Task> getSubtasksSync(int userId, int parentTaskId);

    // ===== SEARCH =====
    @Query("SELECT * FROM tasks WHERE userId = :userId AND " +
           "(title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%') " +
           "ORDER BY id DESC")
    LiveData<List<Task>> searchTasks(int userId, String query);

    // ===== FILTER BY TAG =====
    @Query("SELECT * FROM tasks WHERE userId = :userId AND tags LIKE '%' || :tag || '%' ORDER BY id DESC")
    LiveData<List<Task>> getTasksByTag(int userId, String tag);

    // ===== COMPLEX FILTER =====
    @Query("SELECT * FROM tasks WHERE userId = :userId " +
           "AND (:priority IS NULL OR priority = :priority) " +
           "AND (:isCompleted IS NULL OR isCompleted = :isCompleted) " +
           "AND (:tag IS NULL OR tags LIKE '%' || :tag || '%') " +
           "AND (deadline >= :startDate OR :startDate = 0) " +
           "AND (deadline <= :endDate OR :endDate = 0) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'priority' THEN " +
           "  CASE priority WHEN 'high' THEN 1 WHEN 'medium' THEN 2 WHEN 'low' THEN 3 ELSE 4 END " +
           "ELSE id END DESC")
    LiveData<List<Task>> getTasksFiltered(int userId, String priority, Boolean isCompleted, 
                                          String tag, long startDate, long endDate, String sortBy);

    // ===== STATISTICS =====
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1")
    LiveData<Integer> getCompletedTasksCount(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 0")
    LiveData<Integer> getPendingTasksCount(int userId);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND deadline > 0 AND deadline < :now AND isCompleted = 0")
    LiveData<Integer> getOverdueTasksCount(int userId, long now);

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1 AND createdAt >= :startDate AND createdAt <= :endDate")
    LiveData<Integer> getCompletedTasksCountByDate(int userId, long startDate, long endDate);

    // ===== STATISTICS BY PRIORITY =====
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND priority = :priority AND isCompleted = 1")
    LiveData<Integer> getCompletedTasksCountByPriority(int userId, String priority);

    // ===== STATISTICS BY TAG =====
    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 1")
    LiveData<List<Task>> getCompletedTasks(int userId);
}
