package com.example.personaltaskmanager.features.calendar_events.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R

/**
 * Adapter hiển thị TODO con trong Calendar
 * Group theo Task:
 *
 * NT118
 *  - Làm lab 1
 *  - Viết báo cáo
 */
class CalendarTodoAdapter :
    RecyclerView.Adapter<CalendarTodoAdapter.TodoViewHolder>() {

    private val data = mutableListOf<Pair<String, String>>() // TaskTitle - TodoText

    fun setData(grouped: Map<String, List<String>>) {
        data.clear()
        grouped.forEach { (task, todos) ->
            todos.forEach { todo ->
                data.add(task to todo)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feature_calendar_item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val (task, todo) = data[position]
        holder.tvTask.text = task
        holder.tvTodo.text = todo
    }

    override fun getItemCount(): Int = data.size

    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTask: TextView = view.findViewById(R.id.tv_task_title)
        val tvTodo: TextView = view.findViewById(R.id.tv_todo_text)
    }
}
