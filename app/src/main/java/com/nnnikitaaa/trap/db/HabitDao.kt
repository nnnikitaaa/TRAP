@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused", "unused")

package com.nnnikitaaa.trap.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate

@Suppress("unused", "unused")
@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabit(habitId: Long): HabitEntity

    @Query("SELECT * FROM habits WHERE name = :name")
    suspend fun getHabit(name: String): HabitEntity?

    @Query("SELECT * FROM habits WHERE name = :name AND id != :excludeId")
    suspend fun getHabit(name: String, excludeId: Long): HabitEntity?

    @Query("SELECT * FROM habits WHERE startDate <= :startDate")
    suspend fun getAllHabits(startDate: LocalDate): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE startDate <= :startDate AND id NOT IN (:excludeIds)")
    suspend fun getAllHabits(startDate: LocalDate, excludeIds: List<Long>): List<HabitEntity>
}