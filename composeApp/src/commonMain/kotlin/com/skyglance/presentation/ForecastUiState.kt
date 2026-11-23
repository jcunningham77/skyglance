package com.skyglance.presentation

import com.skyglance.domain.Forecast
import com.skyglance.domain.TempUnit
import kotlin.time.Instant

sealed interface ForecastUiState {
    /** No cache, fetch in flight. */
    data object Loading : ForecastUiState

    /**
     * A forecast to show. [staleSince] is non-null only when the last refresh failed and this is
     * cached data — the UI then shows a "Last updated … ago, couldn't refresh" banner.
     */
    data class Content(
        val forecast: Forecast,
        val unit: TempUnit,
        val selectedIndex: Int,
        val isRefreshing: Boolean,
        val staleSince: Instant?,
        val staleReason: String?,
    ) : ForecastUiState

    /** Permission denied and no cache. */
    data class PermissionNeeded(
        val permanently: Boolean,
    ) : ForecastUiState

    /** Platform has no location implementation (iOS stub) and no cache. */
    data object Unsupported : ForecastUiState

    /** Fetch or location failed and no cache. */
    data class Error(
        val message: String,
    ) : ForecastUiState
}
