package com.skyglance.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * A fetched forecast, always stored in Celsius. [fetchedAt] is the "as of" time the UI uses for
 * its "Last updated" label — it says nothing about caching on its own.
 */
@Serializable
data class Forecast(
    val placeName: String?,
    val lat: Double,
    val lon: Double,
    @Serializable(with = InstantIso8601Serializer::class)
    val fetchedAt: Instant,
    val days: List<DayForecast>,
)

/**
 * A forecast is no longer useful once its last day has passed — every day it describes is in the
 * past, not just stale by a few hours. The repository treats this the same as having no cache at
 * all, rather than showing a cached forecast where every day is wrong.
 */
fun Forecast.isExpired(today: LocalDate): Boolean {
    val lastDay = days.lastOrNull()?.date ?: return true
    return lastDay < today
}
