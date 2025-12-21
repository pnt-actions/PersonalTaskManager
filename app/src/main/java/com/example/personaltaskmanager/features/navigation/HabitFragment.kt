package com.example.personaltaskmanager.features.navigation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit
import com.example.personaltaskmanager.features.habit_tracker.screens.HabitAdapter
import com.example.personaltaskmanager.features.habit_tracker.screens.HabitDetailActivity
import com.example.personaltaskmanager.features.habit_tracker.viewmodel.HabitViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class HabitFragment : Fragment() {

    private lateinit var viewModel: HabitViewModel
    private lateinit var rvHabits: RecyclerView
    private lateinit var adapter: HabitAdapter
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feature_habit_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvHabits = view.findViewById(R.id.rv_habits)
        fabAdd = view.findViewById(R.id.fab_add_habit)

        viewModel = ViewModelProvider(requireActivity())[HabitViewModel::class.java]

        setupRecycler()

        fabAdd.setOnClickListener { showAddHabitDialog() }

        viewModel.getAllHabits().observe(viewLifecycleOwner) { habits ->
            adapter.setData(habits)
        }
    }

    private fun setupRecycler() {
        adapter = HabitAdapter(
            { habit -> 
                // Mở màn hình chi tiết habit - sử dụng UUID để đảm bảo chính xác
                val intent = Intent(requireContext(), HabitDetailActivity::class.java)
                intent.putExtra("habit_id", habit.id)
                intent.putExtra("habit_uuid", habit.uuid ?: "")
                startActivity(intent)
            },
            { habit -> viewModel.deleteHabit(habit) },
            { habit -> viewModel.toggleHabitCompleted(habit.id) }
        )

        rvHabits.layoutManager = LinearLayoutManager(requireContext())
        rvHabits.adapter = adapter
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.feature_habit_dialog_create, null)
        val edtTitle = dialogView.findViewById<EditText>(R.id.edt_habit_title)
        val edtDescription = dialogView.findViewById<EditText>(R.id.edt_habit_description)
        val rgTargetType = dialogView.findViewById<RadioGroup>(R.id.rg_target_type)
        val rbFixed = dialogView.findViewById<RadioButton>(R.id.rb_fixed)
        val rbPeriod = dialogView.findViewById<RadioButton>(R.id.rb_period)
        val llFixedDates = dialogView.findViewById<LinearLayout>(R.id.ll_fixed_dates)
        val llPeriod = dialogView.findViewById<LinearLayout>(R.id.ll_period)
        val edtStartDate = dialogView.findViewById<EditText>(R.id.edt_start_date)
        val edtEndDate = dialogView.findViewById<EditText>(R.id.edt_end_date)
        val spinnerPeriod = dialogView.findViewById<Spinner>(R.id.spinner_period)
        val edtDurationMinutes = dialogView.findViewById<EditText>(R.id.edt_duration_minutes)

        // Setup period spinner
        val periodOptions = arrayOf("7 ngày", "15 ngày", "30 ngày", "1 tháng", "2 tháng", "3 tháng", 
            "4 tháng", "5 tháng", "6 tháng", "7 tháng", "8 tháng", "9 tháng", "10 tháng", "11 tháng", "12 tháng")
        val periodAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periodOptions)
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriod.adapter = periodAdapter

        // Toggle visibility based on radio selection
        rgTargetType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_fixed) {
                llFixedDates.visibility = View.VISIBLE
                llPeriod.visibility = View.GONE
            } else {
                llFixedDates.visibility = View.GONE
                llPeriod.visibility = View.VISIBLE
            }
        }

        // Date pickers
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        edtStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                edtStartDate.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        edtEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                edtEndDate.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Thêm Thói quen mới")
            .setView(dialogView)
            .setPositiveButton("Thêm", null)
            .setNegativeButton("Hủy", null)
            .create()

        dialog.setOnShowListener {
            val btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnPositive.setOnClickListener {
                val title = edtTitle.text.toString().trim()
                val description = edtDescription.text.toString().trim()
                val durationText = edtDurationMinutes.text.toString().trim()

                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập tên thói quen", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val durationMinutes = if (durationText.isEmpty()) 0 else durationText.toIntOrNull() ?: 0

                var startDate = System.currentTimeMillis()
                var endDate = 0L
                var targetType = "fixed"

                if (rbFixed.isChecked) {
                    val startText = edtStartDate.text.toString()
                    val endText = edtEndDate.text.toString()
                    
                    if (startText.isEmpty() || endText.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng chọn ngày bắt đầu và kết thúc", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    try {
                        val parsedStart = dateFormat.parse(startText)
                        val parsedEnd = dateFormat.parse(endText)
                        
                        if (parsedStart == null || parsedEnd == null) {
                            Toast.makeText(requireContext(), "Ngày không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        
                        // Set về đầu ngày (00:00:00)
                        val calStart = Calendar.getInstance()
                        calStart.time = parsedStart
                        calStart.set(Calendar.HOUR_OF_DAY, 0)
                        calStart.set(Calendar.MINUTE, 0)
                        calStart.set(Calendar.SECOND, 0)
                        calStart.set(Calendar.MILLISECOND, 0)
                        
                        val calEnd = Calendar.getInstance()
                        calEnd.time = parsedEnd
                        calEnd.set(Calendar.HOUR_OF_DAY, 0)
                        calEnd.set(Calendar.MINUTE, 0)
                        calEnd.set(Calendar.SECOND, 0)
                        calEnd.set(Calendar.MILLISECOND, 0)
                        
                        startDate = calStart.timeInMillis
                        endDate = calEnd.timeInMillis
                        targetType = "fixed"
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Ngày không hợp lệ", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                } else {
                    val selectedPeriod = spinnerPeriod.selectedItemPosition
                    val calendarStart = Calendar.getInstance()
                    calendarStart.set(Calendar.HOUR_OF_DAY, 0)
                    calendarStart.set(Calendar.MINUTE, 0)
                    calendarStart.set(Calendar.SECOND, 0)
                    calendarStart.set(Calendar.MILLISECOND, 0)
                    startDate = calendarStart.timeInMillis
                    
                    val calendarEnd = Calendar.getInstance()
                    calendarEnd.set(Calendar.HOUR_OF_DAY, 0)
                    calendarEnd.set(Calendar.MINUTE, 0)
                    calendarEnd.set(Calendar.SECOND, 0)
                    calendarEnd.set(Calendar.MILLISECOND, 0)
                    
                    // Tính số ngày dựa trên lựa chọn
                    // Lưu ý: startDate đã là ngày đầu tiên, nên cần trừ 1 ngày để có đúng số ngày
                    // Ví dụ: 7 ngày từ 21/12 -> 27/12 (21, 22, 23, 24, 25, 26, 27) = 7 ngày
                    when (selectedPeriod) {
                        0 -> calendarEnd.add(Calendar.DAY_OF_YEAR, 6)      // 7 ngày: thêm 6 = ngày thứ 7
                        1 -> calendarEnd.add(Calendar.DAY_OF_YEAR, 14)     // 15 ngày: thêm 14 = ngày thứ 15
                        2 -> calendarEnd.add(Calendar.DAY_OF_YEAR, 29)     // 30 ngày: thêm 29 = ngày thứ 30
                        3 -> { // 1 tháng
                            calendarEnd.add(Calendar.MONTH, 1)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1) // Trừ 1 ngày
                        }
                        4 -> { // 2 tháng
                            calendarEnd.add(Calendar.MONTH, 2)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        5 -> { // 3 tháng
                            calendarEnd.add(Calendar.MONTH, 3)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        6 -> { // 4 tháng
                            calendarEnd.add(Calendar.MONTH, 4)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        7 -> { // 5 tháng
                            calendarEnd.add(Calendar.MONTH, 5)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        8 -> { // 6 tháng
                            calendarEnd.add(Calendar.MONTH, 6)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        9 -> { // 7 tháng
                            calendarEnd.add(Calendar.MONTH, 7)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        10 -> { // 8 tháng
                            calendarEnd.add(Calendar.MONTH, 8)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        11 -> { // 9 tháng
                            calendarEnd.add(Calendar.MONTH, 9)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        12 -> { // 10 tháng
                            calendarEnd.add(Calendar.MONTH, 10)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        13 -> { // 11 tháng
                            calendarEnd.add(Calendar.MONTH, 11)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                        14 -> { // 12 tháng
                            calendarEnd.add(Calendar.MONTH, 12)
                            calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                        }
                    }
                    
                    endDate = calendarEnd.timeInMillis
                    targetType = "period"
                }

                viewModel.addHabit(title, description, "#5AE4D9", "⭐", startDate, endDate, targetType, durationMinutes)
                Toast.makeText(requireContext(), "Đã thêm thói quen", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = layoutInflater.inflate(R.layout.feature_habit_dialog_add, null)
        val edtTitle = dialogView.findViewById<EditText>(R.id.edt_habit_title)
        val edtDescription = dialogView.findViewById<EditText>(R.id.edt_habit_description)

        edtTitle.setText(habit.title)
        edtDescription.setText(habit.description)

        AlertDialog.Builder(requireContext())
            .setTitle("Chỉnh sửa Thói quen")
            .setView(dialogView)
            .setPositiveButton("Lưu") { _, _ ->
                val title = edtTitle.text.toString().trim()
                val description = edtDescription.text.toString().trim()

                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập tên thói quen", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                habit.title = title
                habit.description = description
                viewModel.updateHabit(habit)
                Toast.makeText(requireContext(), "Đã cập nhật thói quen", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
