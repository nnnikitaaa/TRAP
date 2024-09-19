package com.nnnikitaaa.trap

import android.content.Context
import java.time.LocalDate

enum class PeriodType(val stringResId: Int) {
  DAILY(R.string.period_daily),
  WEEKLY(R.string.period_weekly),
  MONTHLY(R.string.period_monthly),
  WEEKDAYS(R.string.period_weekdays),
  WEEKENDS(R.string.period_weekends)
}

fun PeriodType.isHabitDay(startDate: LocalDate, date: LocalDate): Boolean {
  if (date.isBefore(startDate)){
    return false;
  }

  return when (this) {
    PeriodType.DAILY -> true
    PeriodType.WEEKLY -> startDate.dayOfWeek == date.dayOfWeek
    PeriodType.MONTHLY -> startDate.dayOfMonth == date.dayOfMonth
    PeriodType.WEEKDAYS -> date.dayOfWeek.value in 1..5
    PeriodType.WEEKENDS -> date.dayOfWeek.value in 6..7
  }
}

fun isHabitDay(habit: Habit, date: LocalDate): Boolean{
  return habit.period.isHabitDay(habit.startDate, date)
}

fun PeriodType.toLocalizedString(context: Context): String {
  return context.getString(this.stringResId)
}
