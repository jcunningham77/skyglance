package com.skyglance.ui.preview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.skyglance.theme.WeatherTheme

/** Trivial preview confirming the Compose preview pipeline renders for this scaffold. */
@Preview
@Composable
internal fun ScaffoldPreview_Light() {
    WeatherTheme(darkTheme = false) {
        Text("Scaffold preview")
    }
}

@Preview
@Composable
internal fun ScaffoldPreview_Dark() {
    WeatherTheme(darkTheme = true) {
        Text("Scaffold preview")
    }
}
