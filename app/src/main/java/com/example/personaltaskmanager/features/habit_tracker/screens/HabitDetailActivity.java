package com.example.personaltaskmanager.features.habit_tracker.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit;
import com.example.personaltaskmanager.features.habit_tracker.data.model.HabitCompletion;
import com.example.personaltaskmanager.features.habit_tracker.viewmodel.HabitViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;

/**
 * Màn hình chi tiết Habit
 * Hiển thị: ngày đã hoàn thành, ngày đã miss, completion percentage
 */
public class HabitDetailActivity extends AppCompatActivity {

    private static final long DAY_IN_MILLIS = 86400000L;

    private HabitViewModel viewModel;
    private int habitId;
    private String habitUuid;
    private Habit currentHabit;
    private List<HabitCompletion> currentCompletions;

    private TextView tvHabitTitle, tvHabitDescription, tvHabitDuration, tvHabitPeriod;
    private ProgressBar progressCompletion;
    private TextView tvCompletionPercent;
    private RecyclerView rvCompletedDays, rvMissedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feature_habit_detail);

        habitId = getIntent().getIntExtra("habit_id", -1);
        habitUuid = getIntent().getStringExtra("habit_uuid");
        
        if (habitId == -1 && (habitUuid == null || habitUuid.isEmpty())) {
            finish();
            return;
        }

        // Reset dữ liệu
        currentHabit = null;
        currentCompletions = null;

        initViews();
        setupViewModel();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        tvHabitTitle = findViewById(R.id.tv_habit_title);
        tvHabitDescription = findViewById(R.id.tv_habit_description);
        tvHabitDuration = findViewById(R.id.tv_habit_duration);
        tvHabitPeriod = findViewById(R.id.tv_habit_period);
        progressCompletion = findViewById(R.id.progress_completion);
        tvCompletionPercent = findViewById(R.id.tv_completion_percent);
        rvCompletedDays = findViewById(R.id.rv_completed_days);
        rvMissedDays = findViewById(R.id.rv_missed_days);

        rvCompletedDays.setLayoutManager(new GridLayoutManager(this, 7));
        rvMissedDays.setLayoutManager(new GridLayoutManager(this, 7));
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        // Ưu tiên dùng ID vì ID luôn đáng tin cậy hơn
        // Nếu không có ID, mới dùng UUID
        LiveData<Habit> habitLiveData;
        
        if (habitId != -1) {
            // Có ID -> dùng ID (đáng tin cậy nhất)
            habitLiveData = viewModel.getHabitById(habitId);
        } else if (habitUuid != null && !habitUuid.isEmpty()) {
            // Không có ID -> dùng UUID
            habitLiveData = viewModel.getHabitByUuid(habitUuid);
        } else {
            // Không có cả ID và UUID -> không thể load
            finish();
            return;
        }

        habitLiveData.observe(this, habit -> {
            if (habit == null) {
                finish();
                return;
            }

            // Verify habit
            boolean isValid = false;
            if (habitId != -1) {
                // Verify bằng ID
                isValid = habit.id == habitId;
            } else if (habitUuid != null && !habitUuid.isEmpty()) {
                // Verify bằng UUID
                // Nếu habit.uuid null hoặc empty (habit cũ), accept nếu ID match (nếu có)
                if (habit.uuid == null || habit.uuid.isEmpty()) {
                    // Habit cũ không có UUID -> accept luôn (sẽ được update UUID sau)
                    isValid = true;
                } else {
                    // Habit mới có UUID -> verify bằng UUID
                    isValid = habitUuid.equals(habit.uuid);
                }
                // Update habitId từ habit loaded để dùng cho completions
                if (isValid) {
                    habitId = habit.id;
                }
            }

            if (isValid) {
                currentHabit = habit;
                habitId = habit.id; // Đảm bảo habitId luôn được set
                
                // Nếu habit không có UUID (habit cũ), tự động generate và update
                if (currentHabit.uuid == null || currentHabit.uuid.isEmpty()) {
                    currentHabit.uuid = java.util.UUID.randomUUID().toString();
                    viewModel.updateHabit(currentHabit);
                }
                
                updateHabitInfo();
                
                // Sau khi có habit, mới observe completions
                observeCompletions();
            } else {
                finish();
            }
        });
    }

    private void observeCompletions() {
        // Chỉ observe khi đã có habitId hợp lệ
        if (habitId == -1 || currentHabit == null) {
            return;
        }

        // Remove observer cũ nếu có để tránh duplicate
        viewModel.getCompletionsByHabit(habitId).observe(this, completions -> {
            if (currentHabit != null && currentHabit.id == habitId) {
                currentCompletions = completions != null ? completions : new ArrayList<>();
                updateCompletionData();
            }
        });
    }

    private void updateHabitInfo() {
        if (currentHabit == null) return;

        tvHabitTitle.setText(currentHabit.title);
        tvHabitDescription.setText(
            currentHabit.description.isEmpty() ? "Không có mô tả" : currentHabit.description
        );
        tvHabitDuration.setText("Thời gian: " + currentHabit.durationMinutes + " phút/ngày");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (currentHabit.endDate > 0) {
            String startStr = dateFormat.format(new Date(currentHabit.startDate));
            String endStr = dateFormat.format(new Date(currentHabit.endDate));
            tvHabitPeriod.setText("Thời gian: Từ " + startStr + " đến " + endStr);
        } else {
            tvHabitPeriod.setText("Thời gian: Không giới hạn");
        }
    }

    private void updateCompletionData() {
        if (currentHabit == null || currentCompletions == null) {
            return;
        }

        // Tính toán startDate và endDate
        long currentTime = System.currentTimeMillis();
        long currentDay = normalizeToDayStart(currentTime);

        // Xác định startDate: LUÔN dùng startDate nếu có, không dùng createdAt
        long startDate;
        if (currentHabit.startDate > 0) {
            startDate = normalizeToDayStart(currentHabit.startDate);
        } else {
            // Nếu startDate = 0 (habit cũ), dùng hôm nay làm startDate
            startDate = currentDay;
        }

        // Xác định endDate: nếu = 0 thì dùng hôm nay
        long endDate;
        if (currentHabit.endDate > 0) {
            endDate = normalizeToDayStart(currentHabit.endDate);
        } else {
            endDate = currentDay;
        }

        // Đảm bảo startDate <= endDate
        if (startDate > endDate) {
            startDate = endDate;
        }

        // Tính TỔNG SỐ NGÀY trong khoảng thời gian đã chọn (từ startDate đến endDate)
        // Không chỉ tính đến hôm nay, mà tính cả tương lai
        long totalDays = ((endDate - startDate) / DAY_IN_MILLIS) + 1;
        if (totalDays <= 0) {
            totalDays = 1;
        }

        // Lấy các ngày đã hoàn thành (trong toàn bộ khoảng startDate đến endDate)
        Set<Long> completedDaysSet = new HashSet<>();
        for (HabitCompletion completion : currentCompletions) {
            long completionDay = normalizeToDayStart(completion.completionDate);
            if (completionDay >= startDate && completionDay <= endDate) {
                completedDaysSet.add(completionDay);
            }
        }

        // Tính completion percentage dựa trên TỔNG SỐ NGÀY đã chọn
        // Ví dụ: 12 tháng = 365 ngày, nếu đã hoàn thành 1 ngày = 1/365 = 0.27%
        int completedCount = completedDaysSet.size();
        int completionPercent = 0;
        if (totalDays > 0) {
            completionPercent = (int) ((completedCount * 100.0) / totalDays);
            if (completionPercent > 100) {
                completionPercent = 100;
            }
        }

        // actualEndDate chỉ dùng để tính missed days (chỉ tính đến hôm nay)
        long actualEndDate = Math.min(endDate, currentDay);

        // Cập nhật UI
        progressCompletion.setProgress(completionPercent);
        // Hiển thị: "X / Y ngày (Z%)"
        String completionText = completedCount + " / " + totalDays + " ngày (" + completionPercent + "%)";
        tvCompletionPercent.setText(completionText);

        // Tạo danh sách ngày đã hoàn thành
        List<Long> completedDaysList = new ArrayList<>(completedDaysSet);
        completedDaysList.sort(Long::compareTo);
        DayAdapter completedAdapter = new DayAdapter(completedDaysList, true);
        rvCompletedDays.setAdapter(completedAdapter);

        // Tạo danh sách ngày đã miss (chỉ từ startDate đến actualEndDate, KHÔNG tính trước startDate)
        List<Long> missedDaysList = new ArrayList<>();
        
        // Chỉ tính nếu startDate <= currentDay (không tính nếu startDate ở tương lai)
        if (startDate <= currentDay) {
            // Đảm bảo chỉ tính đến hôm nay
            long maxDate = Math.min(actualEndDate, currentDay);
            
            // Bắt đầu từ startDate (đã được normalize) - KHÔNG tính trước startDate
            long dayToCheck = startDate;
            
            while (dayToCheck <= maxDate) {
                // Chỉ thêm vào miss nếu:
                // 1. Từ startDate trở đi (dayToCheck >= startDate) - đảm bảo không tính trước
                // 2. Đến hôm nay (dayToCheck <= currentDay) - không tính tương lai
                // 3. Chưa hoàn thành
                if (dayToCheck >= startDate && dayToCheck <= currentDay && !completedDaysSet.contains(dayToCheck)) {
                    missedDaysList.add(dayToCheck);
                }
                // Tăng lên 1 ngày
                dayToCheck += DAY_IN_MILLIS;
                
                // Safety check để tránh infinite loop
                if (dayToCheck > maxDate + DAY_IN_MILLIS) {
                    break;
                }
            }
        }

        DayAdapter missedAdapter = new DayAdapter(missedDaysList, false);
        rvMissedDays.setAdapter(missedAdapter);
    }

    /**
     * Làm tròn timestamp về đầu ngày (00:00:00)
     * Đảm bảo tính toán chính xác theo UTC để tránh timezone issues
     */
    private long normalizeToDayStart(long timestamp) {
        // Sử dụng Calendar để đảm bảo tính toán chính xác
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo update lại khi vào lại activity
        if (currentHabit != null && currentCompletions != null) {
            updateCompletionData();
        }
    }

    /**
     * Adapter để hiển thị danh sách ngày
     */
    private static class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {
        private final List<Long> days;
        private final boolean isCompleted;

        DayAdapter(List<Long> days, boolean isCompleted) {
            this.days = days != null ? days : new ArrayList<>();
            this.isCompleted = isCompleted;
        }

        @Override
        public DayViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new DayViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DayViewHolder holder, int position) {
            long day = days.get(position);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            holder.textView.setText(dateFormat.format(new Date(day)));
            holder.textView.setBackgroundColor(isCompleted ? 
                    0xFF4CAF50 : 0xFFFF5722); // Green for completed, Red for missed
            holder.textView.setTextColor(0xFFFFFFFF);
            holder.textView.setPadding(8, 8, 8, 8);
        }

        @Override
        public int getItemCount() {
            return days.size();
        }

        static class DayViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            DayViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }
}
