package com.example.personaltaskmanager.features.calendar_events.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.personaltaskmanager.features.calendar_events.data.local.entity.CalendarEventEntity;
import com.example.personaltaskmanager.features.calendar_events.data.repository.CalendarRepository;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.data.repository.TaskRepository;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlock;
import com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks.NotionBlockParser;

import java.util.ArrayList;
import java.util.List;

/**
 * CalendarViewModel
 * -----------------
 * ViewModel chính cho Calendar.
 *
 * Chức năng:
 *  - Lấy Event thường
 *  - Lấy Task lớn theo deadline
 *  - Tách Todo con có deadline từ notesJson
 */
public class CalendarViewModel extends AndroidViewModel {

    private final CalendarRepository calendarRepo;
    private final TaskRepository taskRepo;

    public CalendarViewModel(@NonNull Application app) {
        super(app);
        calendarRepo = new CalendarRepository(app);
        taskRepo = new TaskRepository(app);
    }

    /**
     * Event thường (nếu có)
     */
    public LiveData<List<CalendarEventEntity>> getEventsByDate(
            long start,
            long end
    ) {
        return calendarRepo.getEventsByDate(start, end);
    }

    /**
     * 🔥 API CHÍNH cho Calendar UI
     *
     * Trả về danh sách String hiển thị:
     *  - Task lớn
     *  - Task + Todo con
     *
     * Ví dụ:
     *  - NT118
     *  - NT118 - Làm lab 1
     */
    public LiveData<List<String>> getCalendarItemsByDate(
            long start,
            long end
    ) {

        MediatorLiveData<List<String>> result = new MediatorLiveData<>();

        result.addSource(taskRepo.getTasksByDate(start, end), tasks -> {

            List<String> items = new ArrayList<>();

            if (tasks == null) {
                result.setValue(items);
                return;
            }

            for (Task task : tasks) {

                // 1️⃣ Task lớn
                items.add(task.getTitle());

                // 2️⃣ Todo con
                List<NotionBlock> blocks =
                        NotionBlockParser.fromJson(task.getNotesJson());

                for (NotionBlock block : blocks) {
                    if (block.type == NotionBlock.Type.TODO &&
                            block.deadline > 0 &&
                            block.deadline >= start &&
                            block.deadline < end) {

                        items.add(task.getTitle() + " - " + block.text);
                    }
                }
            }

            result.setValue(items);
        });

        return result;
    }
}
