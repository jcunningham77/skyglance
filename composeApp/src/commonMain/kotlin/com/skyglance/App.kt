package com.skyglance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.skyglance.theme.WeatherTheme

/**
 * Scaffold placeholder. Replaced by ForecastRoute once Phase 1 (UI with previews) builds the
 * real screen.
 */
@Composable
fun App() {
    WeatherTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "skyglance scaffold ready",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}
