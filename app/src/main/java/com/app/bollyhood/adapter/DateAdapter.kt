package com.app.bollyhood.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ItemDateBinding
import com.app.bollyhood.model.DateModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateAdapter(
    private val context: Context,
    private val dateList: List<DateModel>,
    private val onDateSelected: (Int) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    var selectedPosition: Int = -1 // Default no selection

    inner class DateViewHolder(val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: DateModel, position: Int) {
            binding.tvDay.text = date.day
            binding.tvMonth.text = date.month

            // Highlight the selected date
            if (position == selectedPosition) {
                binding.root.setBackgroundResource(R.drawable.bg_selected_date)
                binding.tvDay.setTextColor(Color.WHITE)
                binding.tvMonth.setTextColor(Color.WHITE)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_unselected_date)
                binding.tvDay.setTextColor(Color.BLACK)
                binding.tvMonth.setTextColor(Color.BLACK)
            }

            // Handle item click
            binding.root.setOnClickListener {
                if (selectedPosition != position) {
                    val previousPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousPosition) // Update previous selection
                    notifyItemChanged(selectedPosition) // Update new selection
                    onDateSelected(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dateList[position], position)
    }

    override fun getItemCount() = dateList.size

    fun getTodayPosition(): Int {
        val calendar = Calendar.getInstance()
        val todayDate = SimpleDateFormat("d/MMM/yyyy", Locale.getDefault()).format(calendar.time)
        return dateList.indexOfFirst { it.fullDate == todayDate }
    }

    fun scrollToToday(recyclerView: RecyclerView) {
        val todayPosition = getTodayPosition()
        if (todayPosition != -1) {
            selectedPosition = todayPosition
            recyclerView.post {
                (recyclerView.layoutManager as? LinearLayoutManager)
                    ?.scrollToPositionWithOffset(todayPosition, 0)
                notifyItemChanged(todayPosition)
            }
        }
    }
}
