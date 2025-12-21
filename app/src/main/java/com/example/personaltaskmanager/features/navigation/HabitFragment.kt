package com.example.personaltaskmanager.features.navigation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit
import com.example.personaltaskmanager.features.habit_tracker.screens.HabitAdapter
import com.example.personaltaskmanager.features.habit_tracker.viewmodel.HabitViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
            { habit -> showEditHabitDialog(habit) },
            { habit -> viewModel.deleteHabit(habit) },
            { habit -> viewModel.toggleHabitCompleted(habit.id) }
        )

        rvHabits.layoutManager = LinearLayoutManager(requireContext())
        rvHabits.adapter = adapter
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.feature_habit_dialog_add, null)
        val edtTitle = dialogView.findViewById<EditText>(R.id.edt_habit_title)
        val edtDescription = dialogView.findViewById<EditText>(R.id.edt_habit_description)

        AlertDialog.Builder(requireContext())
            .setTitle("Thêm Thói quen mới")
            .setView(dialogView)
            .setPositiveButton("Thêm") { _, _ ->
                val title = edtTitle.text.toString().trim()
                val description = edtDescription.text.toString().trim()

                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng nhập tên thói quen", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.addHabit(title, description, "#5AE4D9", "⭐")
                Toast.makeText(requireContext(), "Đã thêm thói quen", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
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
