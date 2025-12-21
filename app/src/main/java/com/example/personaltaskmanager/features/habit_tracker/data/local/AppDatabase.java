package com.example.personaltaskmanager.features.habit_tracker.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.personaltaskmanager.features.habit_tracker.data.local.dao.HabitDao;
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit;
import com.example.personaltaskmanager.features.habit_tracker.data.model.HabitCompletion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {Habit.class, HabitCompletion.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract HabitDao habitDao();

    /** Executor để chạy insert/update/delete ở background */
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "habit_tracker_db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
