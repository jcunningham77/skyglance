package com.skyglance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.skyglance.domain.Condition
import com.skyglance.domain.DayForecast
import com.skyglance.domain.Forecast
import com.skyglance.domain.TempUnit
import com.skyglance.presentation.ForecastUiState
import com.skyglance.theme.WeatherTheme
import com.skyglance.ui.ForecastContent
import kotlinx.datetime.LocalDate
import kotlin.time.Clock

/**
 * Interactive demo of Phase 1's UI, hoisting selection and unit state in [remember] since there is
 * no ViewModel yet. Replaced by ForecastRoute (backed by [com.skyglance.presentation.ForecastUiState]
 * from a real repository) once the data layer and ViewModel wiring phases land.
 */
@Composable
fun App() {
    var selectedIndex by remember { mutableStateOf(0) }
    var unit by remember { mutableStateOf(TempUnit.Fahrenheit) }

    WeatherTheme {
        ForecastContent(
            state =
                ForecastUiState.Content(
                    forecast = demoForecast,
                    unit = unit,
                    selectedIndex = selectedIndex,
                    isRefreshing = false,
                    staleSince = null,
                    staleReason = null,
                ),
            onDaySelected = { selectedIndex = it },
            onUnitSelected = { unit = it },
            onRetry = {},
            onRequestPermission = {},
            onOpenSettings = {},
        )
    }
}

private fun demoDay(
    date: LocalDate,
    condition: Condition,
    highC: Double,
    lowC: Double,
) = DayForecast(
    date = date,
    condition = condition,
    highC = highC,
    lowC = lowC,
    precipProbabilityMax = 20,
    windMaxKmh = 12.0,
    sunrise = null,
    sunset = null,
)

private val demoForecast =
    Forecast(
        placeName = "Boulder, Colorado",
        lat = 40.0150,
        lon = -105.2705,
        fetchedAt = Clock.System.now(),
        days =
            listOf(
                demoDay(LocalDate(2026, 11, 24), Condition.Clear, 18.0, 6.0),
                demoDay(LocalDate(2026, 11, 25), Condition.PartlyCloudy, 16.0, 5.0),
                demoDay(LocalDate(2026, 11, 26), Condition.Overcast, 14.0, 7.0),
                demoDay(LocalDate(2026, 11, 27), Condition.Rain, 12.0, 8.0),
                demoDay(LocalDate(2026, 11, 28), Condition.Snow, -2.0, -8.0),
                demoDay(LocalDate(2026, 11, 29), Condition.Thunderstorm, 22.0, 15.0),
                demoDay(LocalDate(2026, 11, 30), Condition.Showers, 19.0, 11.0),
            ),
    )
