package com.example.personaltaskmanager.features.habit_tracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.habit_tracker.data.local.AppDatabase;
import com.example.personaltaskmanager.features.habit_tracker.data.local.dao.HabitDao;
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit;
import com.example.personaltaskmanager.features.habit_tracker.data.model.HabitCompletion;

import java.util.List;
import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Repository xử lý logic data cho Habit.
 */
public class HabitRepository {

    private final HabitDao habitDao;
    private final Executor executor;
    private final AuthRepository authRepo;

    public HabitRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        habitDao = db.habitDao();
        executor = newSingleThreadExecutor();
        authRepo = new AuthRepository(app);
    }

    private int getCurrentUserId() {
        User u = authRepo.getCurrentUser();
        return (u != null) ? u.id : -1;
    }

    public LiveData<List<Habit>> getAllHabits() {
        return habitDao.getAllHabits(getCurrentUserId());
    }

    public LiveData<Habit> getHabitById(int habitId) {
        return habitDao.getHabitById(habitId);
    }

    public LiveData<Habit> getHabitByUuid(String uuid) {
        return habitDao.getHabitByUuid(uuid);
    }

    public Habit getHabitByUuidSync(String uuid) {
        return habitDao.getHabitByUuidSync(uuid);
    }

    public void addHabit(Habit habit) {
        executor.execute(() -> {
            habit.userId = getCurrentUserId();
            // Đảm bảo UUID luôn có (nếu chưa có thì generate)
            if (habit.uuid == null || habit.uuid.isEmpty()) {
                habit.uuid = java.util.UUID.randomUUID().toString();
            }
            habitDao.insertHabit(habit);
        });
    }

    public void updateHabit(Habit habit) {
        executor.execute(() -> {
            // Đảm bảo UUID luôn có (nếu chưa có thì generate)
            if (habit.uuid == null || habit.uuid.isEmpty()) {
                habit.uuid = java.util.UUID.randomUUID().toString();
            }
            habitDao.updateHabit(habit);
        });
    }

    public void deleteHabit(Habit habit) {
        executor.execute(() -> {
            habitDao.deleteAllCompletionsForHabit(habit.id);
            habitDao.deleteHabit(habit);
        });
    }

    public LiveData<List<HabitCompletion>> getCompletionsByHabit(int habitId) {
        return habitDao.getCompletionsByHabit(habitId);
    }

    public void markHabitCompleted(int habitId, long date, String note) {
        executor.execute(() -> {
            HabitCompletion completion = new HabitCompletion(habitId, date, note);
            habitDao.insertCompletion(completion);
            updateHabitStreakSync(habitId, date);
        });
    }

    public void unmarkHabitCompleted(int habitId, long date) {
        executor.execute(() -> {
            // Xóa completion trong khoảng thời gian của ngày đó
            long dayStart = (date / 86400000) * 86400000;
            
            // Lấy tất cả completions và tìm cái cần xóa
            List<HabitCompletion> allCompletions = habitDao.getCompletionsByHabitSync(habitId);
            if (allCompletions != null) {
                for (HabitCompletion completion : allCompletions) {
                    long completionDay = (completion.completionDate / 86400000) * 86400000;
                    if (completionDay == dayStart) {
                        habitDao.deleteCompletion(completion);
                        break;
                    }
                }
            }
            
            recalculateStreakSync(habitId);
        });
    }

    private void updateHabitStreakSync(int habitId, long completionDate) {
        // Lấy habit trực tiếp từ database (không dùng LiveData)
        Habit habit = habitDao.getHabitByIdSync(habitId);
        if (habit == null) return;
        
        long lastDate = habit.lastCompletedDate;
        long dayInMillis = 86400000;
        
        // Làm tròn về đầu ngày
        long completionDay = (completionDate / dayInMillis) * dayInMillis;
        long lastDay = lastDate > 0 ? (lastDate / dayInMillis) * dayInMillis : 0;

        if (lastDay == 0) {
            // Lần đầu hoàn thành
            habit.streakDays = 1;
        } else {
            long daysDiff = (completionDay - lastDay) / dayInMillis;
            if (daysDiff == 1) {
                // Liên tiếp (hôm qua -> hôm nay)
                habit.streakDays++;
            } else if (daysDiff == 0) {
                // Cùng ngày - không tăng streak, chỉ cập nhật lastCompletedDate
                // Không làm gì, giữ nguyên streak
            } else {
                // Bị gián đoạn (cách hơn 1 ngày) - reset streak
                habit.streakDays = 1;
            }
        }

        habit.lastCompletedDate = completionDay;
        habitDao.updateHabit(habit);
    }

    private void recalculateStreakSync(int habitId) {
        Habit habit = habitDao.getHabitByIdSync(habitId);
        if (habit == null) return;
        
        // Logic đơn giản: đếm số ngày liên tiếp từ completions
        List<HabitCompletion> completions = habitDao.getCompletionsByHabitSync(habitId);
        if (completions == null || completions.isEmpty()) {
            habit.streakDays = 0;
            habit.lastCompletedDate = 0;
        } else {
            long dayInMillis = 86400000;
            
            // Làm tròn tất cả completion dates về đầu ngày và loại bỏ trùng lặp
            java.util.Set<Long> uniqueDays = new java.util.HashSet<>();
            for (HabitCompletion c : completions) {
                long day = (c.completionDate / dayInMillis) * dayInMillis;
                uniqueDays.add(day);
            }
            
            // Chuyển thành list và sắp xếp giảm dần
            java.util.List<Long> sortedDays = new java.util.ArrayList<>(uniqueDays);
            sortedDays.sort((a, b) -> Long.compare(b, a));
            
            if (sortedDays.isEmpty()) {
                habit.streakDays = 0;
                habit.lastCompletedDate = 0;
            } else {
                // Đếm streak từ ngày mới nhất
                int streak = 1;
                long expectedDate = sortedDays.get(0);
                
                for (int i = 1; i < sortedDays.size(); i++) {
                    long nextDate = sortedDays.get(i);
                    long diff = (expectedDate - nextDate) / dayInMillis;
                    if (diff == 1) {
                        // Liên tiếp
                        streak++;
                        expectedDate = nextDate;
                    } else {
                        // Bị gián đoạn
                        break;
                    }
                }
                
                habit.streakDays = streak;
                habit.lastCompletedDate = sortedDays.get(0);
            }
        }
        
        habitDao.updateHabit(habit);
    }
}

