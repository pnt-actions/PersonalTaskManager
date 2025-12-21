package com.example.personaltaskmanager.features.task_manager.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.task_manager.data.model.Task
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel
import com.example.personaltaskmanager.features.task_manager.screens.workspace.TaskWorkspaceActivity

class TaskListFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var rvTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var spinnerFilter: Spinner
    private lateinit var fabAdd: View
    private lateinit var edtSearch: EditText

    private val REQUEST_ADD_TASK = 2001
    private var allTasks: List<Task> = emptyList()
    private var currentFilter = FilterType.ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.feature_task_manager_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTasks = view.findViewById(R.id.rv_tasks)
        spinnerFilter = view.findViewById(R.id.spinner_filter)
        fabAdd = view.findViewById(R.id.fab_add_task)
        edtSearch = view.findViewById(R.id.edt_search)

        viewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        setupRecycler()
        setupFilter()
        setupSearch()

        fabAdd.setOnClickListener { openAddTask() }

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            allTasks = tasks
            applyFilters()
        }
    }

    private enum class FilterType {
        ALL, COMPLETED, PENDING, OVERDUE
    }

    private fun setupFilter() {
        val filterOptions = arrayOf("Tất cả", "Đã hoàn thành", "Chưa hoàn thành", "Quá hạn")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        spinnerFilter.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentFilter = when (position) {
                    0 -> FilterType.ALL
                    1 -> FilterType.COMPLETED
                    2 -> FilterType.PENDING
                    3 -> FilterType.OVERDUE
                    else -> FilterType.ALL
                }
                applyFilters()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupSearch() {
        if (edtSearch != null) {
            edtSearch.hint = "Tìm kiếm công việc..."
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    applyFilters()
                }
            })
        }
    }

    private fun applyFilters() {
        var filtered = allTasks

        // Apply search filter
        val searchQuery = edtSearch?.text?.toString()?.trim()?.lowercase()
        if (!searchQuery.isNullOrEmpty()) {
            filtered = filtered.filter {
                it.title.lowercase().contains(searchQuery) ||
                it.description.lowercase().contains(searchQuery)
            }
        }

        // Apply status filter
        filtered = when (currentFilter) {
            FilterType.ALL -> filtered
            FilterType.COMPLETED -> filtered.filter { it.isCompleted }
            FilterType.PENDING -> filtered.filter { !it.isCompleted }
            FilterType.OVERDUE -> filtered.filter {
                !it.isCompleted && it.deadline > 0 && it.deadline < System.currentTimeMillis()
            }
        }

        adapter.setData(filtered)
    }

    private fun setupRecycler() {
        adapter = TaskAdapter(
            { task -> openEditTask(task) },
            { task -> viewModel.deleteTask(task) },
            { task, done -> viewModel.toggleCompleted(task, done) }
        )

        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = adapter
    }

    private fun openAddTask() {
        val intent = Intent(requireContext(), TaskDetailActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_TASK)
    }

    private fun openEditTask(task: Task) {
        val intent = Intent(requireContext(), TaskWorkspaceActivity::class.java)
        intent.putExtra("task_id", task.id)
        intent.putExtra("task_uuid", task.uuid ?: "")
        startActivity(intent)
    }
}
