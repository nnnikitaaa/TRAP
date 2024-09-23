package com.nnnikitaaa.trap.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Dao
interface HabitHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: HabitHistoryEntity)

    @Update
    suspend fun updateHistory(history: HabitHistoryEntity)

    @Query("SELECT * FROM habit_history WHERE date = :date AND habitId = :habitId")
    suspend fun getHistory(date: LocalDate, habitId: Long): HabitHistoryEntity?

    @Query("SELECT * FROM habit_history WHERE date = :date")
    suspend fun getHistoryForDate(date: LocalDate): List<HabitHistoryEntity>
}