package com.example.personaltaskmanager.features.calendar_events.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * TaskCalendarEntity
 * ------------------
 * Bảng trung gian liên kết Task ↔ CalendarEvent.
 *
 * - KHÔNG nối trực tiếp task ↔ calendar_event
 * - Tránh nợ kỹ thuật khi mở rộng
 *
 * Quan hệ:
 * - taskId  -> task.id (task_manager DB, logical)
 * - eventId -> calendar_event.id (calendar_events DB, logical)
 *
 * 1 Task  -> 0..1 CalendarEvent
 */
@Entity(tableName = "task_calendar")
public class TaskCalendarEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /** Logical FK -> task.id */
    public int taskId;

    /** Logical FK -> calendar_event.id */
    public int eventId;

    public TaskCalendarEntity(int taskId, int eventId) {
        this.taskId = taskId;
        this.eventId = eventId;
    }

    /** Constructor rỗng cho Room */
    public TaskCalendarEntity() {}
}
