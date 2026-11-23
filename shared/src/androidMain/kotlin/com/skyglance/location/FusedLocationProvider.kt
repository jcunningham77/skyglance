package com.skyglance.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

/**
 * Coarse location with a timeout, falling back to the last known fix. Weather does not need
 * precise coordinates, and coarse permission is less alarming to users.
 */
internal class FusedLocationProvider(
    private val context: Context,
) : LocationProvider {
    private val client by lazy { LocationServices.getFusedLocationProviderClient(context) }

    override suspend fun currentLocation(): LocationResult {
        if (!hasCoarseLocationPermission()) {
            // false: "never asked" and "denied forever" look identical from a cold check
            // (shouldShowRequestPermissionRationale can't tell them apart either, even with an
            // Activity). The real answer only exists right after a live request's callback, in
            // the permission request UI hook — not here.
            return LocationResult.PermissionDenied(permanently = false)
        }
        val location = currentLocationWithTimeout() ?: lastKnownLocation()
        return location?.let { LocationResult.Found(it.latitude, it.longitude) } ?: LocationResult.Unavailable
    }

    private fun hasCoarseLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission") // guarded by hasCoarseLocationPermission() above
    private suspend fun currentLocationWithTimeout(): Location? {
        val cancellationTokenSource = CancellationTokenSource()
        return try {
            withTimeoutOrNull(TIMEOUT) {
                client.getCurrentLocation(Priority.PRIORITY_LOW_POWER, cancellationTokenSource.token).await()
            }
        } finally {
            cancellationTokenSource.cancel()
        }
    }

    @SuppressLint("MissingPermission") // guarded by hasCoarseLocationPermission() above
    private suspend fun lastKnownLocation(): Location? = runCatching { client.lastLocation.await() }.getOrNull()

    private companion object {
        val TIMEOUT = 10.seconds
    }
}
