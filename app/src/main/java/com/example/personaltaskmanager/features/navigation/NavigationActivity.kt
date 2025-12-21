package com.example.personaltaskmanager.features.navigation

import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.admin.screens.AdminFragment
import com.example.personaltaskmanager.features.calendar_events.screens.CalendarFragment
import com.example.personaltaskmanager.features.navigation.SettingsFragment
import com.example.personaltaskmanager.features.task_manager.screens.TaskListFragment
import com.example.personaltaskmanager.features.navigation.HabitFragment
import com.example.personaltaskmanager.features.dashboard.screens.DashboardFragment
import com.example.personaltaskmanager.features.habit_tracker.data.model.Habit
import com.example.personaltaskmanager.features.habit_tracker.data.model.HabitCompletion
import com.example.personaltaskmanager.features.habit_tracker.viewmodel.HabitViewModel
import java.util.Calendar

class NavigationActivity : AppCompatActivity() {

    private var role: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        setLightStatusBar()

        role = intent.getStringExtra("role") ?: "user"

        val composeNav = findViewById<ComposeView>(R.id.bottom_nav_compose)

        composeNav.setContent {

            var current by remember {
                mutableStateOf(
                    if (role == "admin") AdminNavItem.MANAGE else NavItem.DASHBOARD
                )
            }

            if (role == "admin") {
                AdminBottomNavBar(
                    current = current as AdminNavItem,
                    onSelect = { selected ->
                        current = selected
                        navigateAdminTo(selected)
                    }
                )
            } else {
                BottomNavBar(
                    current = current as NavItem,
                    onSelect = { selected ->
                        current = selected
                        navigateTo(selected)
                    }
                )
            }
        }

        if (role == "admin") {
            navigateAdminTo(AdminNavItem.MANAGE)
        } else {
            navigateTo(NavItem.DASHBOARD)
            // Kiểm tra và hiển thị thông báo về target chưa hoàn thành
            checkIncompleteTargets()
        }
    }

    private fun checkIncompleteTargets() {
        val habitViewModel = ViewModelProvider(this)[HabitViewModel::class.java]
        
        habitViewModel.getAllHabits().observe(this) { habits ->
            if (habits.isNullOrEmpty()) return@observe

            val today = System.currentTimeMillis()
            val dayInMillis = 86400000L
            val dayStart = (today / dayInMillis) * dayInMillis

            val incompleteHabits = mutableListOf<String>()

            // Lọc các habits cần kiểm tra (có target và hôm nay trong khoảng thời gian)
            val habitsToCheck = habits.filter { habit ->
                habit.startDate > 0 && dayStart >= habit.startDate && 
                dayStart <= (if (habit.endDate > 0) habit.endDate else today)
            }

            if (habitsToCheck.isEmpty()) return@observe

            // Đếm số lượng observers đã hoàn thành
            var checkedCount = 0
            val totalToCheck = habitsToCheck.size

            for (habit in habitsToCheck) {
                habitViewModel.getCompletionsByHabit(habit.id).observe(this) { completions ->
                    checkedCount++
                    
                    var completedToday = false
                    if (completions != null) {
                        for (completion in completions) {
                            val completionDay = (completion.completionDate / dayInMillis) * dayInMillis
                            if (completionDay == dayStart) {
                                completedToday = true
                                break
                            }
                        }
                    }

                    if (!completedToday) {
                        incompleteHabits.add(habit.title)
                    }

                    // Khi đã kiểm tra xong tất cả, hiển thị dialog nếu có target chưa hoàn thành
                    if (checkedCount == totalToCheck && incompleteHabits.isNotEmpty()) {
                        showIncompleteTargetsDialog(incompleteHabits)
                    }
                }
            }
        }
    }

    private fun showIncompleteTargetsDialog(habits: List<String>) {
        val message = "Bạn có ${habits.size} target chưa hoàn thành hôm nay:\n\n" +
                habits.joinToString("\n") { "• $it" }

        AlertDialog.Builder(this)
            .setTitle("Nhắc nhở Target")
            .setMessage(message)
            .setPositiveButton("Đã biết", null)
            .show()
    }

    // --------------------- USER NAV -------------------------
    private fun navigateTo(item: NavItem) {
        val fragment: Fragment = when (item) {
            NavItem.DASHBOARD -> DashboardFragment()
            NavItem.TASKS -> TaskListFragment()
            NavItem.CALENDAR -> CalendarFragment()
            NavItem.HABIT -> HabitFragment()
            NavItem.SETTINGS -> SettingsFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_container, fragment)
            .commit()
    }

    // --------------------- ADMIN NAV ------------------------
    private fun navigateAdminTo(item: AdminNavItem) {
        val fragment: Fragment = when (item) {
            AdminNavItem.MANAGE -> AdminFragment()
            AdminNavItem.SETTINGS -> SettingsFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_container, fragment)
            .commit()
    }

    /** Light status bar (giống create task) */
    private fun setLightStatusBar() {
        val window: Window = window
        window.statusBarColor = Color.WHITE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}
