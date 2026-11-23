package com.skyglance.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Open-Meteo's forecast response. Only `daily` is modeled — everything else (latitude, timezone,
 * units, ...) is ignored via `ignoreUnknownKeys`. Every array element is nullable because
 * Open-Meteo can return nulls for individual days; the mapper decides what to do with that.
 */
@Serializable
data class ForecastResponseDto(
    val daily: DailyDto,
)

@Serializable
data class DailyDto(
    val time: List<String?> = emptyList(),
    @SerialName("weather_code") val weatherCode: List<Int?> = emptyList(),
    @SerialName("temperature_2m_max") val temperatureMax: List<Double?> = emptyList(),
    @SerialName("temperature_2m_min") val temperatureMin: List<Double?> = emptyList(),
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int?> = emptyList(),
    @SerialName("wind_speed_10m_max") val windSpeedMax: List<Double?> = emptyList(),
    val sunrise: List<String?> = emptyList(),
    val sunset: List<String?> = emptyList(),
)
