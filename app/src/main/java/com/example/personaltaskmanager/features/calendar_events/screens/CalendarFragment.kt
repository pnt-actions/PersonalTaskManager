package com.example.personaltaskmanager.features.calendar_events.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.task_manager.data.model.Task
import com.example.personaltaskmanager.features.task_manager.screens.TaskAdapter
import com.example.personaltaskmanager.features.task_manager.screens.TaskDetailActivity
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment() {

    private lateinit var tvMonthTitle: TextView
    private lateinit var tvSelectedDate: TextView
    private lateinit var rvCalendar: RecyclerView

    // ⬇️ Task list view
    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton

    private lateinit var viewModel: TaskViewModel

    private var selectedDate: LocalDate = LocalDate.now()
    private var currentMonth: YearMonth = YearMonth.now()

    private val REQUEST_EDIT_TASK = 3010

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.feature_calendar_month, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init calendar views
        tvMonthTitle = view.findViewById(R.id.tv_month_title)
        tvSelectedDate = view.findViewById(R.id.tv_selected_date)
        rvCalendar = view.findViewById(R.id.rv_calendar_days)

        rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)

        btnPrev = view.findViewById(R.id.btn_prev_month)
        btnNext = view.findViewById(R.id.btn_next_month)

        // Init task list
        rvTasks = view.findViewById(R.id.rv_tasks_of_day)
        rvTasks.layoutManager = LinearLayoutManager(requireContext())

        // Task adapter
        taskAdapter = TaskAdapter(
            { task -> openEditTask(task) },                // listener
            { task -> viewModel.deleteTask(task) },        // delete
            { task, done -> viewModel.toggleCompleted(task, done) }   // toggle
        )
        rvTasks.adapter = taskAdapter

        // ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        // Tint buttons
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.calendar_primary)
        btnPrev.setColorFilter(primaryColor)
        btnNext.setColorFilter(primaryColor)

        // Initial load
        loadMonth()
        loadTasksOfDate(selectedDate)

        btnPrev.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            loadMonth()
        }

        btnNext.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            loadMonth()
        }
    }

    private fun loadMonth() {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        tvMonthTitle.text = currentMonth.format(formatter)

        val days = generateCalendarDays(currentMonth)

        rvCalendar.adapter = CalendarDayAdapter(
            days = days,
            selectedDate = selectedDate,
            onClick = { clickedDay ->
                if (clickedDay.isCurrentMonth) {
                    selectedDate = clickedDay.date
                    tvSelectedDate.text = "Công việc ngày: ${selectedDate}"

                    loadMonth()
                    loadTasksOfDate(selectedDate)
                }
            }
        )
    }

    /** Load tasks of selected date */
    private fun loadTasksOfDate(date: LocalDate) {
        val startMillis = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val endMillis = date
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        viewModel.getTasksByDate(startMillis, endMillis)
            .observe(viewLifecycleOwner) { tasks ->
                taskAdapter.setData(tasks)
            }
    }

    /** Khi click vào 1 task từ Calendar → mở Activity chỉnh sửa */
    private fun openEditTask(task: Task) {
        val intent = Intent(requireContext(), TaskDetailActivity::class.java)
        intent.putExtra("task_id", task.getId())
        startActivityForResult(intent, REQUEST_EDIT_TASK)
    }

    /** Calendar generator (giữ nguyên như code cũ) */
    private fun generateCalendarDays(month: YearMonth): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()

        val firstDay = month.atDay(1)
        val totalDays = month.lengthOfMonth()

        val dayOfWeekIndex = firstDay.dayOfWeek.value % 7

        val prevMonth = month.minusMonths(1)
        val prevMonthDays = prevMonth.lengthOfMonth()
        for (i in 1..dayOfWeekIndex) {
            val dayNum = prevMonthDays - (dayOfWeekIndex - i)
            days.add(
                CalendarDay(
                    day = dayNum,
                    isCurrentMonth = false,
                    date = prevMonth.atDay(dayNum)
                )
            )
        }

        for (i in 1..totalDays) {
            days.add(
                CalendarDay(
                    day = i,
                    isCurrentMonth = true,
                    date = month.atDay(i)
                )
            )
        }

        val nextMonth = month.plusMonths(1)
        val remain = 42 - days.size
        for (i in 1..remain) {
            days.add(
                CalendarDay(
                    day = i,
                    isCurrentMonth = false,
                    date = nextMonth.atDay(i)
                )
            )
        }

        return days
    }

}
