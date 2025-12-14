package com.example.personaltaskmanager.features.calendar_events.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.authentication.data.model.User;
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository;
import com.example.personaltaskmanager.features.calendar_events.data.local.dao.CalendarDao;
import com.example.personaltaskmanager.features.calendar_events.data.local.db.CalendarDatabase;
import com.example.personaltaskmanager.features.calendar_events.data.local.entity.CalendarEventEntity;
import com.example.personaltaskmanager.features.calendar_events.data.local.entity.TaskCalendarEntity;

import java.util.List;

/**
 * CalendarRepository
 * ------------------
 * Trung gian giữa CalendarDatabase và ViewModel.
 */
public class CalendarRepository {

    private final CalendarDao dao;
    private final AuthRepository authRepo;

    public CalendarRepository(Context context) {
        dao = CalendarDatabase.getInstance(context).calendarDao();
        authRepo = new AuthRepository(context);
    }

    private int getCurrentUserId() {
        User u = authRepo.getCurrentUser();
        return (u != null) ? u.id : -1;
    }

    public LiveData<List<CalendarEventEntity>> getEventsByDate(
            long start,
            long end
    ) {
        return dao.getEventsByDate(getCurrentUserId(), start, end);
    }

    public void linkTaskToEvent(int taskId, int eventId) {
        dao.linkTaskToEvent(new TaskCalendarEntity(taskId, eventId));
    }
}
