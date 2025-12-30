package com.example.personaltaskmanager.features.habit_tracker.screens;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adapter hiển thị danh sách Habit.
 */
public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList = new ArrayList<>();
    private final OnHabitClickListener listener;
    private final OnHabitDeleteListener deleteListener;
    private final OnHabitToggleListener toggleListener;

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
    }

    public interface OnHabitDeleteListener {
        void onHabitDelete(Habit habit);
    }

    public interface OnHabitToggleListener {
        void onHabitToggle(Habit habit);
    }

    public HabitAdapter(
            OnHabitClickListener listener,
            OnHabitDeleteListener deleteListener,
            OnHabitToggleListener toggleListener
    ) {
        this.listener = listener;
        this.deleteListener = deleteListener;
        this.toggleListener = toggleListener;
    }

    public void setData(List<Habit> habits) {
        // Sắp xếp habits theo:
        // 1. Streak giảm dần (streak cao nhất đứng đầu)
        // 2. Nếu streak bằng nhau, ưu tiên habit được tick trước (lastCompletedDate tăng dần)
        // 3. Nếu cùng streak và cùng lastCompletedDate, ưu tiên habit tạo trước (createdAt tăng dần)
        List<Habit> sortedHabits = new ArrayList<>(habits);
        Collections.sort(sortedHabits, new Comparator<Habit>() {
            @Override
            public int compare(Habit h1, Habit h2) {
                // So sánh theo streak giảm dần
                int streakCompare = Integer.compare(h2.streakDays, h1.streakDays);
                if (streakCompare != 0) {
                    return streakCompare;
                }
                // Nếu streak bằng nhau, so sánh theo lastCompletedDate tăng dần
                // (habit tick trước = lastCompletedDate nhỏ hơn = rank cao hơn)
                int dateCompare = Long.compare(h1.lastCompletedDate, h2.lastCompletedDate);
                if (dateCompare != 0) {
                    return dateCompare;
                }
                // Nếu cùng streak và cùng lastCompletedDate, ưu tiên habit tạo trước
                // (habit tạo trước = createdAt nhỏ hơn = rank cao hơn)
                return Long.compare(h1.createdAt, h2.createdAt);
            }
        });
        this.habitList = sortedHabits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feature_habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        holder.textTitle.setText(habit.title);
        holder.textDescription.setText(habit.description);
        holder.textIcon.setText(habit.icon);
        holder.textStreak.setText("🔥 " + habit.streakDays + " ngày");

        // Xác định rank dựa trên vị trí (đã được sắp xếp theo streak giảm dần)
        int rank = position + 1; // position 0 = rank 1, position 1 = rank 2, etc.
        
        // Set màu nền CardView dựa trên rank
        int cardBackgroundColor;
        if (rank == 1) {
            // Rank 1: Vàng (Hoàng kim)
            cardBackgroundColor = Color.parseColor("#FFD700"); // Gold
        } else if (rank == 2) {
            // Rank 2: Bạc
            cardBackgroundColor = Color.parseColor("#C0C0C0"); // Silver
        } else if (rank == 3) {
            // Rank 3: Đồng
            cardBackgroundColor = Color.parseColor("#CD7F32"); // Bronze
        } else {
            // Các rank khác: Trắng
            cardBackgroundColor = Color.parseColor("#FFFFFF"); // White
        }
        
        holder.cardView.setCardBackgroundColor(cardBackgroundColor);

        // Set màu indicator (giữ nguyên màu gốc của habit)
        try {
            int color = Color.parseColor(habit.color);
            holder.viewColorIndicator.setBackgroundColor(color);
        } catch (Exception e) {
            holder.viewColorIndicator.setBackgroundColor(Color.parseColor("#5AE4D9"));
        }

        // Checkbox - kiểm tra xem đã hoàn thành hôm nay chưa
        long today = System.currentTimeMillis();
        long dayStart = (today / 86400000) * 86400000;
        boolean completedToday = habit.lastCompletedDate >= dayStart && 
                                  habit.lastCompletedDate < dayStart + 86400000;
        
        holder.checkboxHabit.setOnCheckedChangeListener(null);
        holder.checkboxHabit.setChecked(completedToday);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onHabitClick(habit);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onHabitDelete(habit);
        });

        holder.checkboxHabit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (toggleListener != null) toggleListener.onHabitToggle(habit);
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescription, textIcon, textStreak;
        View viewColorIndicator;
        CheckBox checkboxHabit;
        ImageButton btnDelete;
        CardView cardView;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardHabit);
            textTitle = itemView.findViewById(R.id.textHabitTitle);
            textDescription = itemView.findViewById(R.id.textHabitDescription);
            textIcon = itemView.findViewById(R.id.textHabitIcon);
            textStreak = itemView.findViewById(R.id.textHabitStreak);
            viewColorIndicator = itemView.findViewById(R.id.viewColorIndicator);
            checkboxHabit = itemView.findViewById(R.id.checkboxHabit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

