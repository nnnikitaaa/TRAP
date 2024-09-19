package com.nnnikitaaa.trap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(private var habits: List<Habit>, private var context: Context) :
    RecyclerView.Adapter<HabitAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        holder.checkbox.isChecked = currentHabit.completed
        holder.checkbox.isEnabled = currentHabit.enabled
    }
}
