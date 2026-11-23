package com.skyglance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyglance.data.ForecastRepository
import com.skyglance.data.PreferencesRepository
import com.skyglance.data.RefreshFailure
import com.skyglance.data.RefreshResult
import com.skyglance.domain.Forecast
import com.skyglance.domain.TempUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Thin state machine: combines the repository's cached forecast, the outcome of the last
 * [ForecastRepository.refresh] call, the unit preference and the selected day into one
 * [ForecastUiState], per the plan's repository-to-state table. [onScreenStarted], [onRetry] and
 * [onPermissionResult] all call [ForecastRepository.refresh] — [onUnitSelected] and
 * [onDaySelected] never touch the network path.
 */
class ForecastViewModel(
    private val forecastRepository: ForecastRepository,
    private val preferencesRepository: PreferencesRepository,
    private val clock: Clock,
) : ViewModel() {
    private val latestForecast = MutableStateFlow<Forecast?>(null)
    private val refreshState = MutableStateFlow<RefreshState>(RefreshState.Idle)
    private val selectedIndex = MutableStateFlow(0)

    // LocationProvider can't tell "never asked" from "denied forever" apart (see
    // FusedLocationProvider), so it always reports false. The only place that distinction is ever
    // actually knowable is right after a live permission request's callback — onPermissionResult
    // records it here, overriding what the repository reports.
    private val knownPermanentlyDenied = MutableStateFlow(false)

    val uiState: StateFlow<ForecastUiState> =
        combine(latestForecast, preferencesRepository.unit, refreshState, selectedIndex, knownPermanentlyDenied, ::toUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ForecastUiState.Loading)

    init {
        // Reflects whatever is cached immediately, independent of onScreenStarted() below — a
        // ViewModel re-created with a warm cache shouldn't need an explicit refresh to show it.
        viewModelScope.launch {
            forecastRepository.forecast.collect { forecast ->
                latestForecast.value = forecast
                selectedIndex.value = defaultSelectionIndex(forecast)
            }
        }
    }

    fun onScreenStarted() = refresh()

    /** [permanentlyDenied] is only meaningful right after a live permission request's callback —
     * see [com.skyglance.location.LocationAccess]. Defaults to false for callers (e.g. retrying
     * after a cold-check denial) that have no fresher answer than the one already known. */
    fun onPermissionResult(permanentlyDenied: Boolean = knownPermanentlyDenied.value) {
        knownPermanentlyDenied.value = permanentlyDenied
        refresh()
    }

    fun onRetry() = refresh()

    fun onUnitSelected(unit: TempUnit) {
        viewModelScope.launch { preferencesRepository.setUnit(unit) }
    }

    fun onDaySelected(index: Int) {
        val days = latestForecast.value?.days ?: return
        selectedIndex.value = index.coerceIn(days.indices)
    }

    private fun refresh() {
        viewModelScope.launch {
            refreshState.value = RefreshState.Refreshing
            refreshState.value =
                when (val result = forecastRepository.refresh()) {
                    RefreshResult.Success -> RefreshState.Idle
                    is RefreshResult.Failure -> RefreshState.Failed(result.reason)
                }
        }
    }

    /** Defaults to the day whose date is today, or the first day if today isn't in the list
     * (e.g. the mapper dropped it for missing data) — never by position alone. */
    private fun defaultSelectionIndex(forecast: Forecast): Int {
        val today = clock.todayIn(TimeZone.currentSystemDefault())
        val todayIndex = forecast.days.indexOfFirst { it.date == today }
        return if (todayIndex >= 0) todayIndex else 0
    }

    private fun toUiState(
        forecast: Forecast?,
        unit: TempUnit,
        refresh: RefreshState,
        selectedIndex: Int,
        permanentlyDenied: Boolean,
    ): ForecastUiState {
        if (forecast == null) {
            return when (refresh) {
                RefreshState.Idle, RefreshState.Refreshing -> ForecastUiState.Loading
                is RefreshState.Failed -> noForecastFailureState(refresh.reason, permanentlyDenied)
            }
        }
        val failed = refresh as? RefreshState.Failed
        return ForecastUiState.Content(
            forecast = forecast,
            unit = unit,
            // Defensive clamp: a day tapped just as a refresh shrinks the list shouldn't produce
            // an out-of-bounds index, on top of the reset-on-new-forecast behavior above.
            selectedIndex = selectedIndex.coerceIn(forecast.days.indices),
            isRefreshing = refresh == RefreshState.Refreshing,
            staleSince = failed?.let { forecast.fetchedAt },
            staleReason = failed?.let { failureMessage(it.reason) },
        )
    }

    private fun noForecastFailureState(
        reason: RefreshFailure,
        permanentlyDenied: Boolean,
    ): ForecastUiState =
        when (reason) {
            is RefreshFailure.PermissionDenied -> ForecastUiState.PermissionNeeded(permanentlyDenied)
            RefreshFailure.LocationUnsupported -> ForecastUiState.Unsupported
            RefreshFailure.LocationUnavailable, RefreshFailure.Network, RefreshFailure.Server ->
                ForecastUiState.Error(failureMessage(reason))
        }

    private fun failureMessage(reason: RefreshFailure): String =
        when (reason) {
            is RefreshFailure.PermissionDenied -> "Location permission needed to show a forecast."
            RefreshFailure.LocationUnavailable -> "Couldn't get your location."
            RefreshFailure.LocationUnsupported -> "Location isn't available on this platform yet."
            RefreshFailure.Network -> "No connection"
            RefreshFailure.Server -> "Couldn't load the forecast."
        }

    private sealed interface RefreshState {
        data object Idle : RefreshState

        data object Refreshing : RefreshState

        data class Failed(
            val reason: RefreshFailure,
        ) : RefreshState
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
