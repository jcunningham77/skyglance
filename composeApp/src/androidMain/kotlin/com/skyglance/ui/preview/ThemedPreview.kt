package com.skyglance.ui.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skyglance.theme.WeatherTheme

/**
 * Renders [content] once in light and once in dark theme, stacked vertically, instead of needing
 * two preview functions per case. Each copy gets the full preview width — a side-by-side layout
 * squeezes anything wider than a small box (a banner, a 7-box day row) into half-width and
 * garbles the text wrapping.
 */
@Composable
fun ThemedPreview(content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        WeatherTheme(darkTheme = false) { Column(Modifier.fillMaxWidth()) { content() } }
        WeatherTheme(darkTheme = true) { Column(Modifier.fillMaxWidth().padding(top = 8.dp)) { content() } }
    }
}
