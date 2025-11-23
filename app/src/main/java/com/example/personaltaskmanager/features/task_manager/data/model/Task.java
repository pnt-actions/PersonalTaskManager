package com.example.personaltaskmanager.features.task_manager.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public long createdAt;

    // ==== CONSTRUCTOR CHÍNH CHO ROOM ====
    public Task(String title, String description, long createdAt) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    // ==== CONSTRUCTOR PHỤ — KHÔNG CHO ROOM DÙNG ====
    // (dùng để fake data hoặc truyền id khi update)
    @Ignore
    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    // ==== GETTER ====
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    // ==== SETTER (cần cho update) ====
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
