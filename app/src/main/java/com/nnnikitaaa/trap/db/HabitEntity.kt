package com.nnnikitaaa.trap.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nnnikitaaa.trap.habit.Habit
import com.nnnikitaaa.trap.habit.PeriodType
import java.time.LocalDate

@Entity(
    tableName = "habits",
    indices = [Index(value = ["name"], unique = true)]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val period: PeriodType,
    val startDate: LocalDate,
    val active: Boolean
)

fun HabitEntity.toHabit(completed: Boolean, enabled: Boolean, clickable: Boolean): Habit {
    return Habit(
        id = this.id,
        name = this.name,
        period = this.period,
        startDate = this.startDate,
        completed = completed,
        enabled = enabled,
        clickable = clickable
    )
}