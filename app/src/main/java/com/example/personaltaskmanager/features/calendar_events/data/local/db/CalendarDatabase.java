package com.example.personaltaskmanager.features.calendar_events.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.personaltaskmanager.features.calendar_events.data.local.dao.CalendarDao;
import com.example.personaltaskmanager.features.calendar_events.data.local.entity.CalendarEventEntity;
import com.example.personaltaskmanager.features.calendar_events.data.local.entity.ReminderEntity;
import com.example.personaltaskmanager.features.calendar_events.data.local.entity.TaskCalendarEntity;

/**
 * CalendarDatabase
 * ----------------
 * Room Database RIÊNG cho feature Calendar.
 *
 * Chứa:
 * - calendar_events
 * - task_calendar
 * - reminders
 *
 * KHÔNG liên kết vật lý với TaskDatabase / AuthDatabase.
 * Chỉ dùng logical reference (id).
 */
@Database(
        entities = {
                CalendarEventEntity.class,
                TaskCalendarEntity.class,
                ReminderEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class CalendarDatabase extends RoomDatabase {

    private static volatile CalendarDatabase INSTANCE;

    public abstract CalendarDao calendarDao();

    public static CalendarDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CalendarDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    CalendarDatabase.class,
                                    "calendar_events_db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
