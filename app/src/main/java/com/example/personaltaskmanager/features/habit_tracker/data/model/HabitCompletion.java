package com.example.personaltaskmanager.features.habit_tracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

/**
 * Entity HabitCompletion lưu lịch sử hoàn thành habit.
 * Mỗi record đại diện cho một ngày habit được hoàn thành.
 */
@Entity(
    tableName = "habit_completions",
    indices = {@Index("habitId"), @Index("completionDate")}
)
public class HabitCompletion {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /** FK -> habits.id */
    public int habitId;

    /** Ngày hoàn thành (millis, chỉ lưu ngày, không có giờ) */
    public long completionDate;

    /** Ghi chú (optional) */
    public String note = "";

    public HabitCompletion(int habitId, long completionDate, String note) {
        this.habitId = habitId;
        this.completionDate = completionDate;
        this.note = note;
    }

    /** Constructor rỗng cho Room */
    public HabitCompletion() {}

    public int getId() { return id; }
    public int getHabitId() { return habitId; }
    public long getCompletionDate() { return completionDate; }
    public String getNote() { return note; }
}

