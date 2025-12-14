package com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks;

public class NotionBlock {

    public enum Type {
        PARAGRAPH,
        TODO,
        BULLET,
        DIVIDER,
        FILE
    }

    public String id;
    public Type type;
    public String text;
    public boolean isChecked;

    // FILE
    public String fileUri;
    public String fileName;

    // ✅ TODO DEADLINE (millis)
    public long deadline = 0;

    public NotionBlock(String id, Type type, String text, boolean isChecked) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.isChecked = isChecked;
    }
}
