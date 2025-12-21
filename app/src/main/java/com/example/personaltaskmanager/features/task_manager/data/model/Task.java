package com.example.personaltaskmanager.features.task_manager.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

/**
 * Entity Task lưu trong Room Database.
 * Giữ nguyên cấu trúc đang có, chỉ bổ sung userId để phân tách task theo từng user.
 */
@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /** UUID v4 duy nhất cho mỗi task - đảm bảo không nhầm lẫn */
    public String uuid;

    public String title;
    public String description;
    public long createdAt;

    public boolean isCompleted = false;

    public long deadline = 0L;

    public String notesJson = "";
    public String tablesJson = "";

    /** userId giúp tách dữ liệu theo từng account */
    public int userId;

    /** URI ảnh công việc (Gallery) */
    public String imageUri;   // ✅ THÊM

    // ==== CONSTRUCTOR CHÍNH CHO ROOM ====
    public Task(String title,
                String description,
                long createdAt,
                long deadline,
                String notesJson,
                String tablesJson,
                int userId) {

        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;

        this.notesJson = notesJson;
        this.tablesJson = tablesJson;

        this.userId = userId;
        this.isCompleted = false;
        this.imageUri = null; // ✅
        // Generate UUID v4
        this.uuid = java.util.UUID.randomUUID().toString();
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

        this.userId = 0;
        this.isCompleted = false;
        this.imageUri = null; // ✅
        // Generate UUID v4
        this.uuid = java.util.UUID.randomUUID().toString();
    }

    /** Constructor rỗng cho Room */
    public Task() {
        // Generate UUID khi tạo mới
        this.uuid = java.util.UUID.randomUUID().toString();
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
    public int getUserId() { return userId; }
    public String getImageUri() { return imageUri; } // ✅
    public String getUuid() { return uuid; }

    // ==== SETTER ====
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    public void setDeadline(long deadline) { this.deadline = deadline; }
    public void setNotesJson(String notesJson) { this.notesJson = notesJson; }
    public void setTablesJson(String tablesJson) { this.tablesJson = tablesJson; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; } // ✅

    // ==== SETTER CHO UUID ====
    public void setUuid(String uuid) { this.uuid = uuid; }
}
