package com.nnnikitaaa.trap.habit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nnnikitaaa.trap.R

class HabitAdapter(
    private var habits: MutableList<Habit>,
    private var context: Context,
    private val onCheckBoxCheckedChange: (Habit, Int, Boolean) -> Unit,
    private val onClicked: (Habit, Int) -> Unit
) :
    RecyclerView.Adapter<HabitAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.habitTitle)
        val description: TextView = view.findViewById(R.id.habitDescription)
        val checkbox: CheckBox = view.findViewById(R.id.habitCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.habit_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return habits.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHabit = habits[position]
        holder.title.text = currentHabit.name
        holder.description.text = currentHabit.period.toLocalizedString(context)
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isEnabled = currentHabit.enabled
        holder.checkbox.isChecked = currentHabit.completed
        holder.view.isClickable = currentHabit.clickable

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            onCheckBoxCheckedChange(currentHabit, position, isChecked)
        }
        holder.view.setOnClickListener {
            onClicked(currentHabit, position)
        }
    }

    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}
