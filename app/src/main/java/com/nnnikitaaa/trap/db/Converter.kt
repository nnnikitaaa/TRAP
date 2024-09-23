@file:Suppress("unused", "unused", "unused", "unused", "unused")

package com.nnnikitaaa.trap.db

import androidx.room.TypeConverter
import com.nnnikitaaa.trap.habit.PeriodType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Suppress("unused", "unused", "unused", "unused", "unused", "unused")
class Converter {
    private val localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromPeriodType(periodType: PeriodType): String {
        return periodType.name
    }

    @TypeConverter
    fun toPeriodType(periodType: String): PeriodType {
        return PeriodType.valueOf(periodType)
    }

    @TypeConverter
    fun fromLocalDate(localDate: LocalDate): String? {
        return localDate.format(localDateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let {
            LocalDate.parse(it, localDateFormatter)
        }
    }
}
