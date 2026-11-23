package com.skyglance.location

/**
 * Temporary stand-in bound in DI until the Android location phase adds a real
 * FusedLocationProviderClient-backed implementation (and the iOS stub). Always reports
 * [LocationResult.Unavailable], so [com.skyglance.data.ForecastRepository.refresh] fails
 * predictably instead of the app crashing on a missing binding.
 */
internal class UnimplementedLocationProvider : LocationProvider {
    override suspend fun currentLocation(): LocationResult = LocationResult.Unavailable
}
