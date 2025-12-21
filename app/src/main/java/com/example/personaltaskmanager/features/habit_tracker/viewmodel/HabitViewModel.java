package com.example.personaltaskmanager.features.habit_tracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit;
import com.example.personaltaskmanager.features.habit_tracker.data.model.HabitCompletion;
import com.example.personaltaskmanager.features.habit_tracker.data.repository.HabitRepository;

import java.util.List;

/**
 * ViewModel cho Habit.
 */
public class HabitViewModel extends AndroidViewModel {

    private final HabitRepository repo;

    public HabitViewModel(Application app) {
        super(app);
        repo = new HabitRepository(app);
    }

    public LiveData<List<Habit>> getAllHabits() {
        return repo.getAllHabits();
    }

    public LiveData<Habit> getHabitById(int habitId) {
        return repo.getHabitById(habitId);
    }

    public LiveData<Habit> getHabitByUuid(String uuid) {
        return repo.getHabitByUuid(uuid);
    }

    public void addHabit(String title, String description, String color, String icon) {
        Habit habit = new Habit(
            title,
            description,
            System.currentTimeMillis(),
            color,
            icon,
            0 // userId sẽ được set trong repository
        );
        repo.addHabit(habit);
    }

    public void addHabit(String title, String description, String color, String icon,
                         long startDate, long endDate, String targetType, int durationMinutes) {
        Habit habit = new Habit(
            title,
            description,
            System.currentTimeMillis(),
            color,
            icon,
            0 // userId sẽ được set trong repository
        );
        habit.startDate = startDate;
        habit.endDate = endDate;
        habit.targetType = targetType;
        habit.durationMinutes = durationMinutes;
        repo.addHabit(habit);
    }

    public void updateHabit(Habit habit) {
        repo.updateHabit(habit);
    }

    public void deleteHabit(Habit habit) {
        repo.deleteHabit(habit);
    }

    public LiveData<List<HabitCompletion>> getCompletionsByHabit(int habitId) {
        return repo.getCompletionsByHabit(habitId);
    }

    public void markHabitCompleted(int habitId, long date, String note) {
        repo.markHabitCompleted(habitId, date, note);
    }

    public void unmarkHabitCompleted(int habitId, long date) {
        repo.unmarkHabitCompleted(habitId, date);
    }

    public void toggleHabitCompleted(int habitId) {
        long today = System.currentTimeMillis();
        // Làm tròn về đầu ngày
        long dayStart = (today / 86400000) * 86400000;
        
        // Kiểm tra xem đã hoàn thành hôm nay chưa bằng cách kiểm tra completions
        LiveData<List<HabitCompletion>> completionsLiveData = repo.getCompletionsByHabit(habitId);
        List<HabitCompletion> completions = completionsLiveData.getValue();
        
        boolean alreadyCompleted = false;
        if (completions != null) {
            for (HabitCompletion completion : completions) {
                long completionDay = (completion.completionDate / 86400000) * 86400000;
                if (completionDay == dayStart) {
                    alreadyCompleted = true;
                    break;
                }
            }
        }
        
        if (alreadyCompleted) {
            repo.unmarkHabitCompleted(habitId, dayStart);
        } else {
            repo.markHabitCompleted(habitId, dayStart, "");
        }
    }
}

