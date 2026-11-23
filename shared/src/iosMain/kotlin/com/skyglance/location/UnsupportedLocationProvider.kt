package com.skyglance.location

/** iOS stub: location is not yet implemented on this platform. */
internal class UnsupportedLocationProvider : LocationProvider {
    override suspend fun currentLocation(): LocationResult = LocationResult.Unsupported
}
