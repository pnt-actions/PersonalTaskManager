package com.example.personaltaskmanager.features.calendar_events.screens

import java.time.LocalDate

data class CalendarDay(
    val day: Int,
    val isCurrentMonth: Boolean,
    val date: LocalDate,
    val isValid: Boolean = true    // NEW FIELD: xác định ô có phải là ngày thật không
)
