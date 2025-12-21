package com.example.personaltaskmanager.features.dashboard.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.habit_tracker.viewmodel.HabitViewModel
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var habitViewModel: HabitViewModel

    private lateinit var tvCompletedTasks: TextView
    private lateinit var tvPendingTasks: TextView
    private lateinit var tvOverdueTasks: TextView
    private lateinit var tvCompletedThisWeek: TextView
    private lateinit var tvCompletedThisMonth: TextView
    private lateinit var tvHabitCompletionRate: TextView
    private lateinit var tvLongestStreak: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feature_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]
        habitViewModel = ViewModelProvider(requireActivity())[HabitViewModel::class.java]

        initViews(view)
        observeData()
    }

    private fun initViews(view: View) {
        tvCompletedTasks = view.findViewById(R.id.tv_completed_tasks)
        tvPendingTasks = view.findViewById(R.id.tv_pending_tasks)
        tvOverdueTasks = view.findViewById(R.id.tv_overdue_tasks)
        tvCompletedThisWeek = view.findViewById(R.id.tv_completed_this_week)
        tvCompletedThisMonth = view.findViewById(R.id.tv_completed_this_month)
        tvHabitCompletionRate = view.findViewById(R.id.tv_habit_completion_rate)
        tvLongestStreak = view.findViewById(R.id.tv_longest_streak)
    }

    private fun observeData() {
        // Task statistics
        taskViewModel.getCompletedTasksCount().observe(viewLifecycleOwner) { count ->
            tvCompletedTasks.text = count.toString()
        }

        taskViewModel.getPendingTasksCount().observe(viewLifecycleOwner) { count ->
            tvPendingTasks.text = count.toString()
        }

        taskViewModel.getOverdueTasksCount().observe(viewLifecycleOwner) { count ->
            tvOverdueTasks.text = count.toString()
        }

        // Completed this week
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val weekEnd = calendar.timeInMillis

        taskViewModel.getCompletedTasksCountByDate(weekStart, weekEnd).observe(viewLifecycleOwner) { count ->
            tvCompletedThisWeek.text = count.toString()
        }

        // Completed this month
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val monthEnd = calendar.timeInMillis

        taskViewModel.getCompletedTasksCountByDate(monthStart, monthEnd).observe(viewLifecycleOwner) { count ->
            tvCompletedThisMonth.text = count.toString()
        }

        // Habit statistics
        habitViewModel.getAllHabits().observe(viewLifecycleOwner) { habits ->
            if (habits.isNullOrEmpty()) {
                tvHabitCompletionRate.text = "0%"
                tvLongestStreak.text = "0"
                return@observe
            }

            // Calculate average completion rate
            var totalCompletion = 0.0
            var habitsWithData = 0
            var longestStreak = 0

            for (habit in habits) {
                if (habit.endDate > 0 && habit.startDate > 0) {
                    val totalDays = ((habit.endDate - habit.startDate) / 86400000L) + 1
                    if (totalDays > 0) {
                        // Get completions count (simplified - should use actual completion data)
                        val completionRate = (habit.streakDays.toDouble() / totalDays) * 100
                        totalCompletion += completionRate
                        habitsWithData++
                    }
                }
                if (habit.streakDays > longestStreak) {
                    longestStreak = habit.streakDays
                }
            }

            val avgCompletion = if (habitsWithData > 0) {
                (totalCompletion / habitsWithData).toInt()
            } else {
                0
            }

            tvHabitCompletionRate.text = "$avgCompletion%"
            tvLongestStreak.text = longestStreak.toString()
        }
    }
}

