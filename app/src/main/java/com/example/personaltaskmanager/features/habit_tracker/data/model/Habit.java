package com.example.personaltaskmanager.features.habit_tracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

/**
 * Entity Habit lưu trong Room Database.
 * Đại diện cho một thói quen cần theo dõi.
 */
@Entity(tableName = "habits")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public long createdAt;

    /** Màu sắc của habit (hex color) */
    public String color = "#5AE4D9";

    /** Icon của habit (resource name hoặc emoji) */
    public String icon = "⭐";

    /** userId giúp tách dữ liệu theo từng account */
    public int userId;

    /** Thời gian nhắc nhở (millis trong ngày, -1 nếu không có) */
    public long reminderTime = -1;

    /** Có bật nhắc nhở không */
    public boolean hasReminder = false;

    /** Số ngày liên tiếp hoàn thành */
    public int streakDays = 0;

    /** Ngày hoàn thành gần nhất (millis) */
    public long lastCompletedDate = 0;

    // ==== CONSTRUCTOR CHÍNH CHO ROOM ====
    public Habit(String title,
                String description,
                long createdAt,
                String color,
                String icon,
                int userId) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.color = color;
        this.icon = icon;
        this.userId = userId;
        this.hasReminder = false;
        this.reminderTime = -1;
        this.streakDays = 0;
        this.lastCompletedDate = 0;
    }

    /** Constructor rỗng cho Room */
    public Habit() {}

    // ==== GETTER ====
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getCreatedAt() { return createdAt; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
    public int getUserId() { return userId; }
    public long getReminderTime() { return reminderTime; }
    public boolean isHasReminder() { return hasReminder; }
    public int getStreakDays() { return streakDays; }
    public long getLastCompletedDate() { return lastCompletedDate; }
}

