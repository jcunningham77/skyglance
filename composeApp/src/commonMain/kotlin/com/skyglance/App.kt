package com.skyglance

import androidx.compose.runtime.Composable
import com.skyglance.theme.WeatherTheme
import com.skyglance.ui.ForecastRoute

@Composable
fun App() {
    WeatherTheme {
        ForecastRoute()
    }
}
