package com.example.personaltaskmanager.features.calendar_events.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * CalendarEventEntity
 * -------------------
 * Đại diện cho 1 sự kiện trong Calendar.
 *
 * - Có thể là event độc lập (meeting, lịch cá nhân, habit...)
 * - Hoặc event sinh ra từ Task (qua bảng task_calendar)
 *
 * LƯU Ý:
 * - userId là logical FK -> users.id (AuthDatabase)
 * - KHÔNG enforce foreign key vật lý (multi DB)
 */
@Entity(tableName = "calendar_events")
public class CalendarEventEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /** Logical FK -> users.id */
    public int userId;

    public String title;
    public String description;

    /** Thời gian bắt đầu sự kiện */
    public long startTime;

    /** Thời gian kết thúc sự kiện */
    public long endTime;

    /** Địa điểm (có thể null) */
    public String location;

    /** Ghi chú thêm (có thể null) */
    public String note;

    // ===== CONSTRUCTOR CHO ROOM =====
    public CalendarEventEntity(int userId,
                               String title,
                               String description,
                               long startTime,
                               long endTime,
                               String location,
                               String note) {

        this.userId = userId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.note = note;
    }

    /** Constructor rỗng (Room cần trong vài trường hợp) */
    public CalendarEventEntity() {}
}
