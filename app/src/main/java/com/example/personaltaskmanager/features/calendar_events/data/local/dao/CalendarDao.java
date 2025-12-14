package com.example.personaltaskmanager.features.calendar_events.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.personaltaskmanager.features.calendar_events.data.local.entity.CalendarEventEntity;
import com.example.personaltaskmanager.features.calendar_events.data.local.entity.ReminderEntity;
import com.example.personaltaskmanager.features.calendar_events.data.local.entity.TaskCalendarEntity;

import java.util.List;

/**
 * CalendarDao
 * -----------
 * DAO duy nhất cho CalendarDatabase.
 */
@Dao
public interface CalendarDao {

    // ===== CALENDAR EVENT =====

    @Insert
    long insertEvent(CalendarEventEntity event);

    @Query("""
        SELECT * FROM calendar_events
        WHERE userId = :userId
        AND startTime < :end
        AND endTime >= :start
        ORDER BY startTime ASC
    """)
    LiveData<List<CalendarEventEntity>> getEventsByDate(
            int userId,
            long start,
            long end
    );

    // ===== TASK <-> CALENDAR =====

    @Insert
    void linkTaskToEvent(TaskCalendarEntity ref);

    @Query("""
        SELECT * FROM task_calendar
        WHERE taskId = :taskId
        LIMIT 1
    """)
    TaskCalendarEntity getEventOfTask(int taskId);

    // ===== REMINDER =====

    @Insert
    void insertReminder(ReminderEntity reminder);

    @Query("""
        SELECT * FROM reminders
        WHERE eventId = :eventId
        AND isActive = 1
        ORDER BY triggerTime ASC
    """)
    List<ReminderEntity> getActiveReminders(int eventId);
}
