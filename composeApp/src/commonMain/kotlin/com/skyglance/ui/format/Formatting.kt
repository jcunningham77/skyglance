package com.skyglance.ui.format

import com.skyglance.domain.TempUnit
import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Instant

fun LocalDate.toWeekdayLabel(): String = dayOfWeek.toLocalizedAbbreviation()

fun LocalDate.toDayLabel(today: LocalDate): String = if (this == today) "Today" else toWeekdayLabel()

fun LocalDate.toFullDateLabel(): String {
    val monthLabel = (month.ordinal + 1).toString().padStart(2, '0')
    val dayLabel = day.toString().padStart(2, '0')
    return "$monthLabel/$dayLabel"
}

fun formatTemperature(
    celsius: Double,
    unit: TempUnit,
): String {
    val value = if (unit == TempUnit.Celsius) celsius else celsius * 9 / 5 + 32
    val symbol = if (unit == TempUnit.Celsius) "°C" else "°F"
    return "${value.roundToInt()}$symbol"
}

fun formatWindSpeed(
    kmh: Double,
    unit: TempUnit,
): String {
    val value = if (unit == TempUnit.Celsius) kmh else kmh * 0.621371
    val label = if (unit == TempUnit.Celsius) "km/h" else "mph"
    return "${value.roundToInt()} $label"
}

fun formatStaleMessage(
    staleSince: Instant,
    reason: String? = null,
    now: Instant = Clock.System.now(),
): String {
    val elapsed = now - staleSince
    val agoLabel =
        when {
            elapsed.inWholeHours >= 1 -> "${elapsed.inWholeHours} h ago"
            elapsed.inWholeMinutes >= 1 -> "${elapsed.inWholeMinutes} min ago"
            else -> "just now"
        }
    val base = "Last updated $agoLabel, couldn't refresh"
    return if (reason != null) "$base ($reason)" else base
}
