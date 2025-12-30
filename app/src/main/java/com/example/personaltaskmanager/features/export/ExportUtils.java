package com.example.personaltaskmanager.features.export;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportUtils {

    public static boolean exportTasksToCSV(Context context, List<Task> tasks) {
        try {
            File exportDir = getDownloadsDirectory(context);
            if (exportDir == null || !exportDir.exists()) {
                Toast.makeText(context, "Không thể truy cập thư mục Downloads", Toast.LENGTH_SHORT).show();
                return false;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(exportDir, "PersonalTaskManager_tasks_" + timestamp + ".csv");

            FileWriter writer = new FileWriter(file);
            
            // Header
            writer.append("ID,Title,Description,Priority,Tags,Status,Deadline,Created At\n");

            // Data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            for (Task task : tasks) {
                writer.append(String.valueOf(task.getId())).append(",");
                writer.append(escapeCSV(task.getTitle())).append(",");
                writer.append(escapeCSV(task.getDescription())).append(",");
                writer.append(task.getPriority() != null ? task.getPriority() : "medium").append(",");
                writer.append(escapeCSV(String.join(", ", task.getTagsList()))).append(",");
                writer.append(task.isCompleted() ? "Completed" : "Pending").append(",");
                writer.append(task.getDeadline() > 0 ? dateFormat.format(new Date(task.getDeadline())) : "").append(",");
                writer.append(dateFormat.format(new Date(task.getCreatedAt()))).append("\n");
            }

            writer.flush();
            writer.close();

            String message = "Đã xuất " + tasks.size() + " task\n" +
                    "File: " + file.getName() + "\n" +
                    "Đường dẫn: " + file.getAbsolutePath();
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            return true;
        } catch (IOException e) {
            Toast.makeText(context, "Lỗi khi xuất file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean exportHabitsToCSV(Context context, List<Habit> habits) {
        try {
            File exportDir = getDownloadsDirectory(context);
            if (exportDir == null || !exportDir.exists()) {
                Toast.makeText(context, "Không thể truy cập thư mục Downloads", Toast.LENGTH_SHORT).show();
                return false;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(exportDir, "PersonalTaskManager_habits_" + timestamp + ".csv");

            FileWriter writer = new FileWriter(file);
            
            // Header
            writer.append("ID,Title,Description,Streak Days,Start Date,End Date,Target Type,Duration Minutes\n");

            // Data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            for (Habit habit : habits) {
                writer.append(String.valueOf(habit.getId())).append(",");
                writer.append(escapeCSV(habit.getTitle())).append(",");
                writer.append(escapeCSV(habit.getDescription())).append(",");
                writer.append(String.valueOf(habit.getStreakDays())).append(",");
                writer.append(habit.getStartDate() > 0 ? dateFormat.format(new Date(habit.getStartDate())) : "").append(",");
                writer.append(habit.getEndDate() > 0 ? dateFormat.format(new Date(habit.getEndDate())) : "").append(",");
                writer.append(habit.getTargetType() != null ? habit.getTargetType() : "").append(",");
                writer.append(String.valueOf(habit.getDurationMinutes())).append("\n");
            }

            writer.flush();
            writer.close();

            String message = "Đã xuất " + habits.size() + " habit\n" +
                    "File: " + file.getName() + "\n" +
                    "Đường dẫn: " + file.getAbsolutePath();
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            return true;
        } catch (IOException e) {
            Toast.makeText(context, "Lỗi khi xuất file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) return "";
        // Escape quotes and wrap in quotes if contains comma or newline
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Lấy thư mục Downloads, hỗ trợ cả Android 10+ (Scoped Storage)
     */
    private static File getDownloadsDirectory(Context context) {
        // Thử lấy thư mục Downloads công khai
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        
        if (downloadsDir != null) {
            // Tạo thư mục nếu chưa tồn tại
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            
            // Kiểm tra quyền ghi (cho Android 9 trở xuống)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (downloadsDir.canWrite()) {
                    return downloadsDir;
                }
            } else {
                // Android 10+ có thể ghi vào Downloads mà không cần permission
                return downloadsDir;
            }
        }
        
        // Fallback: dùng external files dir (luôn có quyền ghi)
        File fallbackDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (fallbackDir != null) {
            if (!fallbackDir.exists()) {
                fallbackDir.mkdirs();
            }
            return fallbackDir;
        }
        
        return null;
    }
}

