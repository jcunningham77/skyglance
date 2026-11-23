package com.skyglance.domain

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
