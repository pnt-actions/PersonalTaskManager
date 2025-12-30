package com.example.personaltaskmanager.features.task_manager.screens

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.task_manager.data.model.Task
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel
import com.example.personaltaskmanager.features.task_manager.screens.workspace.TaskWorkspaceActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class TaskListFragment : Fragment() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var rvTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var spinnerFilter: Spinner
    private lateinit var fabAdd: View
    private lateinit var edtSearch: EditText
    private lateinit var btnFilterAdvanced: ImageButton
    private lateinit var btnSavedFilters: ImageButton

    private val REQUEST_ADD_TASK = 2001
    private var allTasks: List<Task> = emptyList()
    private var currentFilter = FilterType.ALL
    
    // Advanced filter state
    private var filterPriority: String? = null
    private var filterStatus: Boolean? = null
    private var filterTag: String? = null
    private var filterStartDate: Long? = null
    private var filterEndDate: Long? = null
    
    // Saved filters
    private val prefs: SharedPreferences by lazy {
        requireContext().getSharedPreferences("task_filters", Context.MODE_PRIVATE)
    }
    private val gson = Gson()
    
    data class SavedFilter(
        val name: String,
        val priority: String?,
        val status: Boolean?,
        val tag: String?,
        val startDate: Long?,
        val endDate: Long?
    )

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
        btnFilterAdvanced = view.findViewById(R.id.btn_filter_advanced)
        btnSavedFilters = view.findViewById(R.id.btn_saved_filters)

        viewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        setupRecycler()
        setupFilter()
        setupSearch()
        setupAdvancedFilter()
        setupSavedFilters()

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

        // Apply search filter (tìm trong title, description, tags)
        val searchQuery = edtSearch?.text?.toString()?.trim()?.lowercase()
        if (!searchQuery.isNullOrEmpty()) {
            filtered = filtered.filter {
                val titleMatch = it.title.lowercase().contains(searchQuery)
                val descMatch = it.description?.lowercase()?.contains(searchQuery) ?: false
                val tagsMatch = it.getTagsList().any { tag ->
                    tag.lowercase().contains(searchQuery)
                }
                titleMatch || descMatch || tagsMatch
            }
        }

        // Apply status filter (basic spinner)
        filtered = when (currentFilter) {
            FilterType.ALL -> filtered
            FilterType.COMPLETED -> filtered.filter { it.isCompleted }
            FilterType.PENDING -> filtered.filter { !it.isCompleted }
            FilterType.OVERDUE -> filtered.filter {
                !it.isCompleted && it.deadline > 0 && it.deadline < System.currentTimeMillis()
            }
        }
        
        // Apply advanced filters
        filterPriority?.let { priority ->
            filtered = filtered.filter { it.priority == priority }
        }
        
        filterStatus?.let { status ->
            filtered = filtered.filter { it.isCompleted == status }
        }
        
        filterTag?.let { tag ->
            if (tag.isNotEmpty()) {
                filtered = filtered.filter { task ->
                    task.getTagsList().any { it.lowercase().contains(tag.lowercase()) }
                }
            }
        }
        
        filterStartDate?.let { startDate ->
            filtered = filtered.filter { task ->
                task.deadline >= startDate || task.deadline == 0L
            }
        }
        
        filterEndDate?.let { endDate ->
            filtered = filtered.filter { task ->
                task.deadline <= endDate || task.deadline == 0L
            }
        }

        // Sort by priority (high -> medium -> low) then by deadline
        filtered = filtered.sortedWith(compareBy<Task> { task ->
            when (task.priority) {
                "high" -> 1
                "medium" -> 2
                "low" -> 3
                else -> 4
            }
        }.thenBy { it.deadline })

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
    
    private fun setupAdvancedFilter() {
        btnFilterAdvanced.setOnClickListener { showAdvancedFilterDialog() }
    }
    
    private fun setupSavedFilters() {
        btnSavedFilters.setOnClickListener { showSavedFiltersDialog() }
    }
    
    private fun showAdvancedFilterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.feature_task_filter_dialog, null)
        val rgPriority = dialogView.findViewById<RadioGroup>(R.id.rg_priority)
        val rbPriorityAll = dialogView.findViewById<RadioButton>(R.id.rb_priority_all)
        val rbPriorityHigh = dialogView.findViewById<RadioButton>(R.id.rb_priority_high)
        val rbPriorityMedium = dialogView.findViewById<RadioButton>(R.id.rb_priority_medium)
        val rbPriorityLow = dialogView.findViewById<RadioButton>(R.id.rb_priority_low)
        
        val rgStatus = dialogView.findViewById<RadioGroup>(R.id.rg_status)
        val rbStatusAll = dialogView.findViewById<RadioButton>(R.id.rb_status_all)
        val rbStatusCompleted = dialogView.findViewById<RadioButton>(R.id.rb_status_completed)
        val rbStatusPending = dialogView.findViewById<RadioButton>(R.id.rb_status_pending)
        
        val edtTag = dialogView.findViewById<EditText>(R.id.edt_filter_tag)
        val edtStartDate = dialogView.findViewById<EditText>(R.id.edt_filter_start_date)
        val edtEndDate = dialogView.findViewById<EditText>(R.id.edt_filter_end_date)
        val edtSaveFilterName = dialogView.findViewById<EditText>(R.id.edt_save_filter_name)
        val btnSaveFilter = dialogView.findViewById<Button>(R.id.btn_save_filter)
        
        // Set current values
        when (filterPriority) {
            "high" -> rbPriorityHigh.isChecked = true
            "medium" -> rbPriorityMedium.isChecked = true
            "low" -> rbPriorityLow.isChecked = true
            else -> rbPriorityAll.isChecked = true
        }
        
        when (filterStatus) {
            true -> rbStatusCompleted.isChecked = true
            false -> rbStatusPending.isChecked = true
            null -> rbStatusAll.isChecked = true
        }
        
        edtTag.setText(filterTag ?: "")
        
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if (filterStartDate != null) {
            edtStartDate.setText(dateFormat.format(Date(filterStartDate!!)))
        }
        if (filterEndDate != null) {
            edtEndDate.setText(dateFormat.format(Date(filterEndDate!!)))
        }
        
        // Date pickers
        val calendar = Calendar.getInstance()
        edtStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                edtStartDate.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        
        edtEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                edtEndDate.setText(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        
        // Save filter button
        btnSaveFilter.setOnClickListener {
            val filterName = edtSaveFilterName.text.toString().trim()
            if (filterName.isNotEmpty()) {
                saveFilter(filterName)
                Toast.makeText(requireContext(), "Đã lưu filter: $filterName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tên filter", Toast.LENGTH_SHORT).show()
            }
        }
        
        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Bộ lọc nâng cao")
            .setView(dialogView)
            .setPositiveButton("Áp dụng") { _, _ ->
                // Get priority
                filterPriority = when {
                    rbPriorityHigh.isChecked -> "high"
                    rbPriorityMedium.isChecked -> "medium"
                    rbPriorityLow.isChecked -> "low"
                    else -> null
                }
                
                // Get status
                filterStatus = when {
                    rbStatusCompleted.isChecked -> true
                    rbStatusPending.isChecked -> false
                    else -> null
                }
                
                // Get tag
                filterTag = edtTag.text.toString().trim().takeIf { it.isNotEmpty() }
                
                // Get dates
                val startText = edtStartDate.text.toString()
                val endText = edtEndDate.text.toString()
                
                filterStartDate = if (startText.isNotEmpty()) {
                    try {
                        val parsed = dateFormat.parse(startText)
                        if (parsed != null) {
                            val cal = Calendar.getInstance()
                            cal.time = parsed
                            cal.set(Calendar.HOUR_OF_DAY, 0)
                            cal.set(Calendar.MINUTE, 0)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            cal.timeInMillis
                        } else null
                    } catch (e: Exception) { null }
                } else null
                
                filterEndDate = if (endText.isNotEmpty()) {
                    try {
                        val parsed = dateFormat.parse(endText)
                        if (parsed != null) {
                            val cal = Calendar.getInstance()
                            cal.time = parsed
                            cal.set(Calendar.HOUR_OF_DAY, 23)
                            cal.set(Calendar.MINUTE, 59)
                            cal.set(Calendar.SECOND, 59)
                            cal.set(Calendar.MILLISECOND, 999)
                            cal.timeInMillis
                        } else null
                    } catch (e: Exception) { null }
                } else null
                
                applyFilters()
            }
            .setNegativeButton("Hủy", null)
            .setNeutralButton("Xóa bộ lọc") { _, _ ->
                filterPriority = null
                filterStatus = null
                filterTag = null
                filterStartDate = null
                filterEndDate = null
                applyFilters()
            }
            .create()
        
        dialog.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog.show()
    }
    
    private fun showSavedFiltersDialog() {
        val savedFilters = getSavedFilters()
        
        if (savedFilters.isEmpty()) {
            Toast.makeText(requireContext(), "Chưa có filter đã lưu", Toast.LENGTH_SHORT).show()
            return
        }
        
        val filterNames = savedFilters.map { it.name }.toTypedArray()
        
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Filter đã lưu")
            .setItems(filterNames) { _, which ->
                val selectedFilter = savedFilters[which]
                applySavedFilter(selectedFilter)
            }
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa filter") { _, _ ->
                showDeleteFilterDialog(savedFilters)
            }
            .create()
            .also { it.window?.setBackgroundDrawableResource(android.R.color.white) }
            .show()
    }
    
    private fun showDeleteFilterDialog(filters: List<SavedFilter>) {
        val filterNames = filters.map { it.name }.toTypedArray()
        val selectedIndices = booleanArrayOf()
        
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Chọn filter để xóa")
            .setMultiChoiceItems(filterNames, selectedIndices) { _, _, _ -> }
            .setPositiveButton("Xóa") { _, _ ->
                val toDelete = filters.filterIndexed { index, _ -> selectedIndices[index] }
                toDelete.forEach { deleteFilter(it.name) }
                Toast.makeText(requireContext(), "Đã xóa ${toDelete.size} filter", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .create()
            .also { it.window?.setBackgroundDrawableResource(android.R.color.white) }
            .show()
    }
    
    private fun applySavedFilter(filter: SavedFilter) {
        filterPriority = filter.priority
        filterStatus = filter.status
        filterTag = filter.tag
        filterStartDate = filter.startDate
        filterEndDate = filter.endDate
        applyFilters()
        Toast.makeText(requireContext(), "Đã áp dụng filter: ${filter.name}", Toast.LENGTH_SHORT).show()
    }
    
    private fun saveFilter(name: String) {
        val filter = SavedFilter(
            name = name,
            priority = filterPriority,
            status = filterStatus,
            tag = filterTag,
            startDate = filterStartDate,
            endDate = filterEndDate
        )
        
        val savedFilters = getSavedFilters().toMutableList()
        savedFilters.removeAll { it.name == name } // Remove if exists
        savedFilters.add(filter)
        
        val json = gson.toJson(savedFilters)
        prefs.edit().putString("saved_filters", json).apply()
    }
    
    private fun getSavedFilters(): List<SavedFilter> {
        val json = prefs.getString("saved_filters", null) ?: return emptyList()
        val type = object : TypeToken<List<SavedFilter>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    private fun deleteFilter(name: String) {
        val savedFilters = getSavedFilters().toMutableList()
        savedFilters.removeAll { it.name == name }
        val json = gson.toJson(savedFilters)
        prefs.edit().putString("saved_filters", json).apply()
    }
}
