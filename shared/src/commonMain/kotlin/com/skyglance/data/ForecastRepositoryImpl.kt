package com.skyglance.data

import com.skyglance.data.mapper.ForecastMappingException
import com.skyglance.data.mapper.toDays
import com.skyglance.domain.Forecast
import com.skyglance.domain.isExpired
import com.skyglance.location.LocationProvider
import com.skyglance.location.LocationResult
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.SerializationException
import kotlin.time.Clock

internal class ForecastRepositoryImpl(
    private val locationProvider: LocationProvider,
    private val weatherApi: WeatherApi,
    private val geocodeApi: GeocodeApi,
    private val cache: ForecastCache,
    private val clock: Clock,
) : ForecastRepository {
    private val refreshMutex = Mutex()

    override val forecast: Flow<Forecast> =
        cache.forecast
            .filterNotNull()
            .filter { !it.isExpired(clock.todayIn(TimeZone.currentSystemDefault())) }

    override suspend fun refresh(): RefreshResult =
        refreshMutex.withLock {
            when (val location = locationProvider.currentLocation()) {
                is LocationResult.Found -> fetchAndStore(location.lat, location.lon)
                is LocationResult.PermissionDenied ->
                    RefreshResult.Failure(RefreshFailure.PermissionDenied(location.permanently))

                LocationResult.Unavailable -> RefreshResult.Failure(RefreshFailure.LocationUnavailable)
                LocationResult.Unsupported -> RefreshResult.Failure(RefreshFailure.LocationUnsupported)
            }
        }

    private suspend fun fetchAndStore(
        lat: Double,
        lon: Double,
    ): RefreshResult =
        coroutineScope {
            val placeNameDeferred = async { geocodeApi.reverseGeocode(lat, lon) }
            val days =
                try {
                    weatherApi.fetchForecast(lat, lon).toDays()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    placeNameDeferred.cancel()
                    return@coroutineScope RefreshResult.Failure(e.toRefreshFailure())
                }
            cache.write(Forecast(placeNameDeferred.await(), lat, lon, clock.now(), days))
            RefreshResult.Success
        }
}

/** Network: no connection or timeout. Server: non-2xx status, unparseable body, or no usable
 * days (from the mapper). Everything else not explicitly a server-side signal is treated as a
 * connectivity problem. */
private fun Exception.toRefreshFailure(): RefreshFailure =
    when (this) {
        is ResponseException, is SerializationException, is ForecastMappingException -> RefreshFailure.Server
        else -> RefreshFailure.Network
    }
