package com.skyglance.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skyglance.domain.TempUnit
import com.skyglance.presentation.ForecastUiState
import com.skyglance.ui.format.formatStaleMessage
import com.skyglance.ui.format.toDayLabel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Stateless screen: renders any [ForecastUiState] with no ViewModel, Koin or repository. Day
 * selection (tap the summary row, or swipe the detail pane) is built here and reported through
 * [onDaySelected] — the ViewModel wiring phase only adds the selection *rules* (default, clamp,
 * reset), not this plumbing.
 */
@Composable
fun ForecastContent(
    state: ForecastUiState,
    onDaySelected: (Int) -> Unit,
    onUnitSelected: (TempUnit) -> Unit,
    onRetry: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            when (state) {
                ForecastUiState.Loading -> LoadingState()
                is ForecastUiState.Content -> ContentState(state, onDaySelected, onUnitSelected, onRetry)
                is ForecastUiState.PermissionNeeded -> PermissionNeededState(state.permanently, onRequestPermission, onOpenSettings)
                ForecastUiState.Unsupported -> UnsupportedState()
                is ForecastUiState.Error -> ErrorState(state.message, onRetry)
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun UnsupportedState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Location is not available on this platform yet.")
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, textAlign = TextAlign.Center)
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun PermissionNeededState(
    permanently: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("skyglance needs your location to show a local forecast.", textAlign = TextAlign.Center)
        if (permanently) {
            Button(onClick = onOpenSettings) { Text("Open settings") }
        } else {
            Button(onClick = onRequestPermission) { Text("Allow location") }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentState(
    state: ForecastUiState.Content,
    onDaySelected: (Int) -> Unit,
    onUnitSelected: (TempUnit) -> Unit,
    onRetry: () -> Unit,
) {
    val days = state.forecast.days
    val selectedIndex = state.selectedIndex.coerceIn(days.indices)
    val pagerState = rememberPagerState(initialPage = selectedIndex) { days.size }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    // Tap: selectedIndex changed externally (a box was tapped, or a new forecast arrived) ->
    // scroll the pager to match it.
    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex) {
            pagerState.animateScrollToPage(selectedIndex)
        }
    }
    // Swipe: the pager settled on a new page -> report it through the same callback tapping uses.
    // The settled page, not the current page, avoids feedback loops while an animation is running.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page -> if (page != selectedIndex) onDaySelected(page) }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LocationHeader(
                placeName = state.forecast.placeName,
                lat = state.forecast.lat,
                lon = state.forecast.lon,
                modifier = Modifier.weight(1f),
            )
            UnitToggle(unit = state.unit, onUnitSelected = onUnitSelected)
        }
        if (state.isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        state.staleSince?.let { staleSince ->
            StaleBanner(message = formatStaleMessage(staleSince, state.staleReason), onRetry = onRetry)
        }
        DaySummaryRow(
            days = days,
            selectedIndex = selectedIndex,
            unit = state.unit,
            onDaySelected = onDaySelected,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val day = days[page]
            DayDetailPane(
                day = day,
                unit = state.unit,
                dayLabel = day.date.toDayLabel(today),
            )
        }
    }
}
