package com.skyglance.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * One day of forecast. [date], [condition], [highC] and [lowC] are required by the mapper — a day
 * missing any of them is dropped before this class is built. The detail fields stay nullable
 * because Open-Meteo does not guarantee a value for them (see the plan's "Why some fields are
 * nullable" note).
 */
@Serializable
data class DayForecast(
    val date: LocalDate,
    val condition: Condition,
    val highC: Double,
    val lowC: Double,
    val precipProbabilityMax: Int?,
    val windMaxKmh: Double?,
    val sunrise: LocalDateTime?,
    val sunset: LocalDateTime?,
)
