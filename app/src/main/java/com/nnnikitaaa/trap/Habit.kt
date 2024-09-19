package com.nnnikitaaa.trap

import java.time.LocalDate

data class Habit(
    val name: String,
    val period: PeriodType,
    val startDate: LocalDate,
    val completed: Boolean,
    val enabled: Boolean = true
)
