package com.skyglance.ui.format

import kotlinx.datetime.DayOfWeek
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

actual fun DayOfWeek.toLocalizedAbbreviation(): String {
    val calendarField =
        when (this) {
            DayOfWeek.MONDAY -> Calendar.MONDAY
            DayOfWeek.TUESDAY -> Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> Calendar.THURSDAY
            DayOfWeek.FRIDAY -> Calendar.FRIDAY
            DayOfWeek.SATURDAY -> Calendar.SATURDAY
            DayOfWeek.SUNDAY -> Calendar.SUNDAY
        }
    return DateFormatSymbols(Locale.getDefault()).shortWeekdays[calendarField]
}
