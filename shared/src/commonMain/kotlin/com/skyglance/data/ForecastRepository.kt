package com.skyglance.data

import com.skyglance.domain.Forecast
import kotlinx.coroutines.flow.Flow

interface ForecastRepository {
    /** Stored forecast, always in °C. Emits immediately if one is stored and again after every
     * successful refresh. Emits nothing until a forecast exists — a cached forecast whose last
     * day has already passed counts as not existing. Survives process death. */
    val forecast: Flow<Forecast>

    /** Gets the location, fetches the forecast and the place name, then writes the cache.
     * Returns a result instead of throwing (CancellationException still propagates). Concurrent
     * calls are serialized with a Mutex, so launch, retry and a permission callback cannot
     * trigger duplicate requests. */
    suspend fun refresh(): RefreshResult
}

sealed interface RefreshResult {
    data object Success : RefreshResult

    data class Failure(
        val reason: RefreshFailure,
    ) : RefreshResult
}

sealed interface RefreshFailure {
    data class PermissionDenied(
        val permanently: Boolean,
    ) : RefreshFailure

    data object LocationUnavailable : RefreshFailure

    data object LocationUnsupported : RefreshFailure

    data object Network : RefreshFailure

    data object Server : RefreshFailure
}
