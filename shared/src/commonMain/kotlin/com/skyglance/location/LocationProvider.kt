package com.skyglance.location

interface LocationProvider {
    suspend fun currentLocation(): LocationResult
}

sealed interface LocationResult {
    data class Found(
        val lat: Double,
        val lon: Double,
    ) : LocationResult

    data class PermissionDenied(
        val permanently: Boolean,
    ) : LocationResult

    data object Unavailable : LocationResult

    // iOS stub
    data object Unsupported : LocationResult
}
