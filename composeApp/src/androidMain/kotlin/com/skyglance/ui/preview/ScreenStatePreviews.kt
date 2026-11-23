package com.skyglance.ui.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.skyglance.domain.TempUnit
import com.skyglance.presentation.ForecastUiState
import com.skyglance.theme.WeatherTheme
import com.skyglance.ui.ForecastContent

private val noop: (Int) -> Unit = {}
private val noopUnit: (TempUnit) -> Unit = {}
private val noopAction: () -> Unit = {}

@Composable
private fun ScreenPreview(
    state: ForecastUiState,
    darkTheme: Boolean,
) {
    WeatherTheme(darkTheme = darkTheme) {
        ForecastContent(
            state = state,
            onDaySelected = noop,
            onUnitSelected = noopUnit,
            onRetry = noopAction,
            onRequestPermission = noopAction,
            onOpenSettings = noopAction,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_Loading_Light() = ScreenPreview(ForecastUiState.Loading, darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_Loading_Dark() = ScreenPreview(ForecastUiState.Loading, darkTheme = true)

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentFresh_Light() = ScreenPreview(PreviewData.contentState, darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentFresh_Dark() = ScreenPreview(PreviewData.contentState, darkTheme = true)

// Static previews can't be tapped or swiped, so this preselects the day with unknown condition,
// a three-digit Fahrenheit temperature and all-null detail fields — the model review checklist's
// edge cases — so they're visible in the full screen without interaction.
private val missingDataSelectedState =
    PreviewData.contentState.copy(
        forecast = PreviewData.forecastMissingDataDay,
        selectedIndex = PreviewData.sevenDaysWithMissingData.lastIndex,
    )

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentMissingDataDaySelected_Light() = ScreenPreview(missingDataSelectedState, darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentMissingDataDaySelected_Dark() = ScreenPreview(missingDataSelectedState, darkTheme = true)

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentRefreshing_Light() = ScreenPreview(PreviewData.contentState.copy(isRefreshing = true), darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentRefreshing_Dark() = ScreenPreview(PreviewData.contentState.copy(isRefreshing = true), darkTheme = true)

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentStale_Light() = ScreenPreview(PreviewData.staleContentState, darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentStale_Dark() = ScreenPreview(PreviewData.staleContentState, darkTheme = true)

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentCelsius_Light() = ScreenPreview(PreviewData.contentState.copy(unit = TempUnit.Celsius), darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_ContentCelsius_Dark() = ScreenPreview(PreviewData.contentState.copy(unit = TempUnit.Celsius), darkTheme = true)

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_PermissionNeeded_CanAskAgain_Light() =
    ScreenPreview(ForecastUiState.PermissionNeeded(permanently = false), darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_PermissionNeeded_CanAskAgain_Dark() =
    ScreenPreview(ForecastUiState.PermissionNeeded(permanently = false), darkTheme = true)

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_PermissionNeeded_Permanent_Light() =
    ScreenPreview(ForecastUiState.PermissionNeeded(permanently = true), darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_PermissionNeeded_Permanent_Dark() =
    ScreenPreview(ForecastUiState.PermissionNeeded(permanently = true), darkTheme = true)

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_Unsupported_Light() = ScreenPreview(ForecastUiState.Unsupported, darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_Unsupported_Dark() = ScreenPreview(ForecastUiState.Unsupported, darkTheme = true)

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_Error_Light() = ScreenPreview(ForecastUiState.Error("Couldn't load the forecast."), darkTheme = false)

@Preview(name = "Dark", widthDp = 360, heightDp = 640)
@Composable
internal fun ScreenPreview_Error_Dark() = ScreenPreview(ForecastUiState.Error("Couldn't load the forecast."), darkTheme = true)
