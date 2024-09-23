package com.nnnikitaaa.trap.datecard

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nnnikitaaa.trap.R
import java.time.format.TextStyle
import java.util.Locale

class DateCardAdapter(
    private var dateCards: List<DateCard>,
    private var context: Context,
    private var selectedItem: Int,
    private val onItemClicked: (DateCard, Int) -> Unit
) : RecyclerView.Adapter<DateCardAdapter.ViewHolder>() {

    private val selectedTextColor = context.getColor(R.color.md_theme_tertiary)
    private val selectedBgColor = context.getColor(R.color.md_theme_tertiaryContainer)
    private val deselectedTextColor = context.getColor(R.color.md_theme_outline)
    private val deselectedBgColor = Color.TRANSPARENT

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val weekDayShort: TextView = view.findViewById(R.id.weekDayShort)
        val monthDay: TextView = view.findViewById(R.id.monthDay)
        val card: CardView = view as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_card, parent, false)

        val dp4 = (4 * context.resources.displayMetrics.density).toInt()
        view.layoutParams.width = (parent.measuredWidth - dp4 * itemCount * 2) / itemCount
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dateCards.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDateCard = dateCards[position]

        holder.weekDayShort.text =
            currentDateCard.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        holder.monthDay.text = currentDateCard.date.dayOfMonth.toString()

        if (position == selectedItem) {
            selectCard(holder)
        } else {
            deselectCard(holder)
        }

        holder.card.setOnClickListener {
            if (selectedItem == position) {
                return@setOnClickListener
            }
            val previousSelected = selectedItem
            selectedItem = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedItem)
            onItemClicked(currentDateCard, position)
        }
    }

    private fun selectCard(holder: ViewHolder) {
        holder.card.setCardBackgroundColor(selectedBgColor)
        holder.card.cardElevation = 2f
        holder.weekDayShort.setTextColor(selectedTextColor)
        holder.monthDay.setTextColor(selectedTextColor)
    }

    private fun deselectCard(holder: ViewHolder) {
        holder.card.setCardBackgroundColor(deselectedBgColor)
        holder.card.cardElevation = 0f
        holder.weekDayShort.setTextColor(deselectedTextColor)
        holder.monthDay.setTextColor(deselectedTextColor)
    }
}
