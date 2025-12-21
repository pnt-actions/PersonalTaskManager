package com.example.personaltaskmanager.features.habit_tracker.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit;
import com.example.personaltaskmanager.features.habit_tracker.data.model.HabitCompletion;

import java.util.List;

/**
 * DAO xử lý CRUD với Room DB cho Habit.
 */
@Dao
public interface HabitDao {

    // ===== HABIT QUERIES =====
    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Habit>> getAllHabits(int userId);

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    LiveData<Habit> getHabitById(int habitId);

    // Lấy habit đồng bộ (dùng trong background thread)
    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    Habit getHabitByIdSync(int habitId);

    // Lấy habit theo UUID (đảm bảo chính xác, không nhầm lẫn)
    @Query("SELECT * FROM habits WHERE uuid = :uuid LIMIT 1")
    LiveData<Habit> getHabitByUuid(String uuid);

    // Lấy habit theo UUID đồng bộ
    @Query("SELECT * FROM habits WHERE uuid = :uuid LIMIT 1")
    Habit getHabitByUuidSync(String uuid);

    @Insert
    long insertHabit(Habit habit);

    @Update
    void updateHabit(Habit habit);

    @Delete
    void deleteHabit(Habit habit);

    // ===== HABIT COMPLETION QUERIES =====
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completionDate DESC")
    LiveData<List<HabitCompletion>> getCompletionsByHabit(int habitId);

    // Lấy completions đồng bộ (dùng trong background thread)
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completionDate DESC")
    List<HabitCompletion> getCompletionsByHabitSync(int habitId);

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND completionDate BETWEEN :start AND :end")
    LiveData<List<HabitCompletion>> getCompletionsByDateRange(int habitId, long start, long end);

    @Insert
    void insertCompletion(HabitCompletion completion);

    @Delete
    void deleteCompletion(HabitCompletion completion);

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    void deleteAllCompletionsForHabit(int habitId);
}

