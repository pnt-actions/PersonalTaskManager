package com.example.personaltaskmanager.features.calendar_events.model

data class CalendarTodoItem(
    val taskId: Int,
    val taskTitle: String,
    val todoText: String,
    val todoDeadline: Long
)
