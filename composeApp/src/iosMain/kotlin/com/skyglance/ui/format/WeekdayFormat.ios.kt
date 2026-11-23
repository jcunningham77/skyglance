package com.skyglance.ui.format

import kotlinx.datetime.DayOfWeek

// Stub: English abbreviations only, same as the common-code placeholder this replaced. iOS only
// needs to compile for now (see the plan's time budget) — swap this for a real NSDateFormatter /
// Locale.current implementation once device testing on iOS is possible.
actual fun DayOfWeek.toLocalizedAbbreviation(): String =
    when (this) {
        DayOfWeek.MONDAY -> "Mon"
        DayOfWeek.TUESDAY -> "Tue"
        DayOfWeek.WEDNESDAY -> "Wed"
        DayOfWeek.THURSDAY -> "Thu"
        DayOfWeek.FRIDAY -> "Fri"
        DayOfWeek.SATURDAY -> "Sat"
        DayOfWeek.SUNDAY -> "Sun"
    }
