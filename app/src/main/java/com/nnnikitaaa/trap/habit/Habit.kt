package com.nnnikitaaa.trap.habit

import java.time.LocalDate

data class Habit(
    val id: Long,
    val name: String,
    val period: PeriodType,
    val startDate: LocalDate,
    val completed: Boolean,
    val clickable: Boolean = true,
    val enabled: Boolean = true
)
