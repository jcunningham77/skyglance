package com.skyglance.ui.preview

import com.skyglance.domain.Condition
import com.skyglance.domain.DayForecast
import com.skyglance.domain.Forecast
import com.skyglance.domain.TempUnit
import com.skyglance.presentation.ForecastUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.time.Instant

/** Sample forecasts for previews, including the edge cases the model review checklist calls out. */
internal object PreviewData {
    private fun day(
        date: LocalDate,
        condition: Condition,
        highC: Double,
        lowC: Double,
        precip: Int? = 20,
        windKmh: Double? = 12.0,
        sunrise: LocalDateTime? = LocalDateTime(date.year, date.month, date.day, 6, 45),
        sunset: LocalDateTime? = LocalDateTime(date.year, date.month, date.day, 19, 30),
    ) = DayForecast(date, condition, highC, lowC, precip, windKmh, sunrise, sunset)

    // The "normal" 7-day forecast every happy-path preview shares. Deliberately free of the
    // Unknown-condition edge case — see sevenDaysWithMissingData for that.
    val sevenDays =
        listOf(
            day(LocalDate(2026, 11, 24), Condition.Clear, 18.0, 6.0),
            day(LocalDate(2026, 11, 25), Condition.PartlyCloudy, 16.0, 5.0),
            day(LocalDate(2026, 11, 26), Condition.Overcast, 14.0, 7.0),
            day(LocalDate(2026, 11, 27), Condition.Rain, 12.0, 8.0, precip = 80),
            day(LocalDate(2026, 11, 28), Condition.Snow, -2.0, -8.0, precip = 60),
            day(LocalDate(2026, 11, 29), Condition.Thunderstorm, 22.0, 15.0, precip = 90),
            day(LocalDate(2026, 11, 30), Condition.Showers, 19.0, 11.0, precip = 70),
        )

    val sixDays = sevenDays.take(6)

    // Edge case, bundled onto one day: unknown condition (WMO mapping table's "anything else"
    // fallback), a three-digit Fahrenheit temperature, and every nullable detail field unset.
    // Only previews named to make that obvious (e.g. *MissingData*) should use this.
    val sevenDaysWithMissingData =
        sevenDays.dropLast(1) +
            day(
                date = LocalDate(2026, 11, 30),
                condition = Condition.Unknown,
                highC = 40.0,
                lowC = 30.0,
                precip = null,
                windKmh = null,
                sunrise = null,
                sunset = null,
            )

    val nullExtrasDay =
        day(LocalDate(2026, 11, 24), Condition.Fog, 10.0, 4.0, precip = null, windKmh = null, sunrise = null, sunset = null)

    // 41C ≈ 106F — three digits in Fahrenheit.
    val threeDigitDay = day(LocalDate(2026, 11, 24), Condition.Clear, 41.0, 28.0)

    val negativeDay = day(LocalDate(2026, 11, 24), Condition.Snow, -5.0, -15.0)

    val fetchedAt = Instant.parse("2026-11-24T12:00:00Z")

    val forecast =
        Forecast(
            placeName = "Boulder, Colorado",
            lat = 40.0150,
            lon = -105.2705,
            fetchedAt = fetchedAt,
            days = sevenDays,
        )

    val forecastMissingDataDay = forecast.copy(days = sevenDaysWithMissingData)

    val forecastLongPlaceName =
        forecast.copy(placeName = "San Juan Capistrano-Dana Point-Laguna Niguel Metro Area")

    val contentState =
        ForecastUiState.Content(
            forecast = forecast,
            unit = TempUnit.Fahrenheit,
            selectedIndex = 0,
            isRefreshing = false,
            staleSince = null,
            staleReason = null,
        )

    val staleContentState =
        contentState.copy(
            staleSince = Instant.parse("2026-11-24T09:00:00Z"),
            staleReason = "No connection",
        )
}
