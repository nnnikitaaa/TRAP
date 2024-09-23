package com.nnnikitaaa.trap.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nnnikitaaa.trap.habit.Habit
import java.time.LocalDate

@Entity(
    tableName = "habit_history",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId"]), Index(value = ["habitId", "date"], unique = true)]
)
data class HabitHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: LocalDate,
    var completed: Boolean
)

fun HabitHistoryEntity.toHabit(habit: HabitEntity, enabled: Boolean): Habit {
    return habit.toHabit(this.completed, enabled)
}