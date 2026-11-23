package com.skyglance.data.mapper

import com.skyglance.data.dto.DailyDto
import com.skyglance.data.dto.ForecastResponseDto
import com.skyglance.domain.DayForecast
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/** Thrown when a forecast response has nothing usable: no valid days once required fields are
 * checked. The repository catches this and reports [com.skyglance.data.RefreshFailure.Server];
 * the stored cache is left untouched either way. */
class ForecastMappingException(
    message: String,
) : Exception(message)

/**
 * Pairs index N of every `daily` array into one [DayForecast]. Every array is index-aligned with
 * `daily.time`, so this never assumes there are 7 of them: it walks only the shared prefix length
 * (the shortest array), and drops any day missing a required value (date, condition, high, low).
 * Detail fields (precipitation, wind, sunrise, sunset) stay null rather than dropping the day.
 */
fun ForecastResponseDto.toDays(): List<DayForecast> {
    val count = daily.sharedPrefixLength()
    val days = (0 until count).mapNotNull { index -> daily.buildDay(index) }
    if (days.isEmpty()) {
        throw ForecastMappingException("No usable days in forecast response")
    }
    return days
}

private fun DailyDto.sharedPrefixLength(): Int = minOf(time.size, weatherCode.size, temperatureMax.size, temperatureMin.size)

private fun DailyDto.buildDay(index: Int): DayForecast? {
    val date = time.getOrNull(index)?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return null
    val weatherCode = weatherCode.getOrNull(index) ?: return null
    val highC = temperatureMax.getOrNull(index) ?: return null
    val lowC = temperatureMin.getOrNull(index) ?: return null
    return DayForecast(
        date = date,
        condition = weatherCode.toCondition(),
        highC = highC,
        lowC = lowC,
        precipProbabilityMax = precipitationProbabilityMax.getOrNull(index),
        windMaxKmh = windSpeedMax.getOrNull(index),
        sunrise = sunrise.getOrNull(index)?.toLocalDateTimeOrNull(),
        sunset = sunset.getOrNull(index)?.toLocalDateTimeOrNull(),
    )
}

private fun String.toLocalDateTimeOrNull(): LocalDateTime? = runCatching { LocalDateTime.parse(this) }.getOrNull()
