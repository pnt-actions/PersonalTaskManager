package com.example.personaltaskmanager.features.calendar_events.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import java.time.LocalDate

class CalendarDayAdapter(
    private val days: List<CalendarDay>,
    private val selectedDate: LocalDate,
    private val onClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarDayAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feature_calendar_item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.bind(day, selectedDate)
        holder.itemView.setOnClickListener {
            if (day.isValid) onClick(day)   // tránh click vào ô padding
        }
    }

    override fun getItemCount(): Int = days.size

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tv_calendar_day)
        private val bgCircle: View = itemView.findViewById(R.id.bg_circle)

        fun bind(day: CalendarDay, selectedDate: LocalDate) {
            val ctx = itemView.context

            // Ô padding (không phải ngày hợp lệ)
            if (!day.isValid) {
                tvDay.text = ""
                bgCircle.visibility = View.INVISIBLE
                return
            }

            tvDay.text = day.date.dayOfMonth.toString()
            bgCircle.visibility = View.VISIBLE

            when {
                // Ngày được chọn
                day.date == selectedDate -> {
                    bgCircle.setBackgroundResource(R.drawable.bg_calendar_selected)
                    tvDay.setTextColor(ctx.getColor(R.color.white))
                }

                // Hôm nay
                day.date == LocalDate.now() -> {
                    bgCircle.setBackgroundResource(R.drawable.bg_calendar_day_today)
                    tvDay.setTextColor(ctx.getColor(R.color.calendar_text_default))
                }

                // Ngày trong tháng bình thường
                day.isCurrentMonth -> {
                    bgCircle.setBackgroundResource(R.drawable.bg_calendar_day_default)
                    tvDay.setTextColor(ctx.getColor(R.color.calendar_text_default))
                }

                // Ngày ngoài tháng
                else -> {
                    bgCircle.setBackgroundResource(R.drawable.bg_calendar_day_default)
                    tvDay.setTextColor(ctx.getColor(R.color.gray_500))
                }
            }
        }
    }
}
