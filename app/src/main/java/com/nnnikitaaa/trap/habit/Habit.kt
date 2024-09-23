package com.nnnikitaaa.trap.habit

import java.time.LocalDate

data class Habit(
    val id: Long,
    val name: String,
    val period: PeriodType,
    val startDate: LocalDate,
    val completed: Boolean,
    val enabled: Boolean = true
)
