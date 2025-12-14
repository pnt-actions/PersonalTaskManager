package com.example.personaltaskmanager.features.calendar_events.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.personaltaskmanager.features.calendar_events.data.local.entity.CalendarEventEntity;
import com.example.personaltaskmanager.features.calendar_events.data.repository.CalendarRepository;

import java.util.List;

/**
 * CalendarViewModel
 * -----------------
 * ViewModel CHÍNH cho Calendar feature.
 */
public class CalendarViewModel extends AndroidViewModel {

    private final CalendarRepository repo;

    public CalendarViewModel(@NonNull Application app) {
        super(app);
        repo = new CalendarRepository(app);
    }

    public LiveData<List<CalendarEventEntity>> getEventsByDate(
            long start,
            long end
    ) {
        return repo.getEventsByDate(start, end);
    }
}
