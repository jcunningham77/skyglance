package com.skyglance.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ForecastTest {
    private val today = LocalDate(2026, 11, 24)

    private fun forecastEndingOn(date: LocalDate): Forecast =
        Forecast(
            placeName = null,
            lat = 0.0,
            lon = 0.0,
            fetchedAt = kotlin.time.Instant.parse("2026-11-20T00:00:00Z"),
            days = listOf(DayForecast(date, Condition.Clear, 20.0, 10.0, null, null, null, null)),
        )

    @Test
    fun notExpiredWhenLastDayIsToday() {
        assertFalse(forecastEndingOn(today).isExpired(today))
    }

    @Test
    fun notExpiredWhenLastDayIsInTheFuture() {
        assertFalse(forecastEndingOn(LocalDate(2026, 11, 25)).isExpired(today))
    }

    @Test
    fun expiredWhenLastDayIsYesterday() {
        assertTrue(forecastEndingOn(LocalDate(2026, 11, 23)).isExpired(today))
    }

    @Test
    fun expiredWhenThereAreNoDaysAtAll() {
        val empty = forecastEndingOn(today).copy(days = emptyList())
        assertTrue(empty.isExpired(today))
    }
}
