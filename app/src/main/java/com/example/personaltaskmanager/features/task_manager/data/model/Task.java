package com.example.personaltaskmanager.features.task_manager.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

/**
 * Entity Task lưu trong Room Database.
 * Các trường:
 *  - id (int)
 *  - title (String)
 *  - description (String)
 *  - createdAt (long)
 *  - isCompleted (boolean)
 *  - deadline (long)
 *
 * Giai đoạn “dọn chỗ”:
 *  - bổ sung notesJson và tablesJson (dự phòng cho 2 feature mới)
 *  - chưa dùng tới nhưng cần trong DB để tránh sửa lớn sau này
 */
@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public long createdAt;

    public boolean isCompleted = false;

    public long deadline = 0L;

    // Dành cho feature Quick Notes (JSON String)
    public String notesJson = "";

    // Dành cho feature Tables (JSON String)
    public String tablesJson = "";

    // ==== CONSTRUCTOR CHÍNH CHO ROOM ====
    public Task(String title, String description, long createdAt, long deadline,
                String notesJson, String tablesJson) {

        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;

        this.notesJson = notesJson;
        this.tablesJson = tablesJson;

        this.isCompleted = false;
    }

    // ==== CONSTRUCTOR PHỤ — KHÔNG CHO ROOM DÙNG ====
    @Ignore
    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;

        this.createdAt = System.currentTimeMillis();
        this.deadline = System.currentTimeMillis();

        this.notesJson = "";
        this.tablesJson = "";

        this.isCompleted = false;
    }

    // ==== GETTER ====
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getCreatedAt() { return createdAt; }
    public boolean isCompleted() { return isCompleted; }
    public long getDeadline() { return deadline; }
    public String getNotesJson() { return notesJson; }
    public String getTablesJson() { return tablesJson; }

    // ==== SETTER ====
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    public void setDeadline(long deadline) { this.deadline = deadline; }

    public void setNotesJson(String notesJson) { this.notesJson = notesJson; }
    public void setTablesJson(String tablesJson) { this.tablesJson = tablesJson; }
}
