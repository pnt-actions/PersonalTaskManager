package com.example.personaltaskmanager.features.calendar_events.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ReminderEntity
 * --------------
 * Đại diện cho 1 lời nhắc của CalendarEvent.
 *
 * - 1 Event có thể có nhiều Reminder
 * - Reminder KHÔNG gắn trực tiếp với Task
 *
 * type:
 * - notification
 * - alarm
 * - email
 */
@Entity(tableName = "reminders")
public class ReminderEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /** Logical FK -> calendar_event.id */
    public int eventId;

    /** Thời điểm kích hoạt reminder */
    public long triggerTime;

    /** Loại reminder: notification | alarm | email */
    public String type;

    /** Bật / tắt reminder */
    public boolean isActive = true;

    public ReminderEntity(int eventId,
                          long triggerTime,
                          String type,
                          boolean isActive) {

        this.eventId = eventId;
        this.triggerTime = triggerTime;
        this.type = type;
        this.isActive = isActive;
    }

    /** Constructor rỗng cho Room */
    public ReminderEntity() {}
}
