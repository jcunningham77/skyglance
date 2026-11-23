package com.skyglance.data

import com.russhwolf.settings.MapSettings
import com.skyglance.data.internal.installDefaults
import com.skyglance.domain.Condition
import com.skyglance.domain.DayForecast
import com.skyglance.domain.Forecast
import com.skyglance.location.LocationProvider
import com.skyglance.location.LocationResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.time.Clock
import kotlin.time.Instant

private const val WEATHER_SUCCESS_BODY =
    """{"daily":{"time":["2026-11-23"],"weather_code":[3],"temperature_2m_max":[10.0],"temperature_2m_min":[2.0],""" +
        """"precipitation_probability_max":[20],"wind_speed_10m_max":[10.0],"sunrise":["2026-11-23T06:55"],"sunset":["2026-11-23T16:39"]}}"""

private const val GEOCODE_SUCCESS_BODY = """{"city":"Boulder","principalSubdivision":"Colorado"}"""

private class FakeLocationProvider(
    private val result: LocationResult,
) : LocationProvider {
    override suspend fun currentLocation(): LocationResult = result
}

private class FixedClock(
    private val instant: Instant,
) : Clock {
    override fun now(): Instant = instant
}

private fun MockRequestHandleScope.jsonResponse(body: String) =
    respond(content = body, status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))

private fun mockHttpClient(
    weatherBody: String? = WEATHER_SUCCESS_BODY,
    geocodeBody: String? = GEOCODE_SUCCESS_BODY,
): HttpClient {
    val engine =
        MockEngine { request ->
            when {
                request.url.toString().contains("open-meteo") ->
                    weatherBody?.let { jsonResponse(it) } ?: respondError(HttpStatusCode.InternalServerError)
                request.url.toString().contains("api-bdc.io") ->
                    geocodeBody?.let { jsonResponse(it) } ?: respondError(HttpStatusCode.InternalServerError)
                else -> error("Unexpected request: ${request.url}")
            }
        }
    return HttpClient(engine) { installDefaults() }
}

private fun repositoryFor(
    locationResult: LocationResult = LocationResult.Found(40.0, -105.0),
    weatherBody: String? = WEATHER_SUCCESS_BODY,
    geocodeBody: String? = GEOCODE_SUCCESS_BODY,
    cache: ForecastCache = ForecastCache(MapSettings()),
    clock: Clock = FixedClock(Instant.parse("2026-11-23T12:00:00Z")),
): ForecastRepository {
    val httpClient = mockHttpClient(weatherBody, geocodeBody)
    return ForecastRepositoryImpl(
        locationProvider = FakeLocationProvider(locationResult),
        weatherApi = WeatherApi(httpClient),
        geocodeApi = GeocodeApi(httpClient),
        cache = cache,
        clock = clock,
    )
}

class ForecastRepositoryTest {
    @Test
    fun successfulRefreshWritesTheCache() =
        runTest {
            val cache = ForecastCache(MapSettings())
            val repository = repositoryFor(cache = cache)

            val result = repository.refresh()

            assertEquals(RefreshResult.Success, result)
            val stored = cache.forecast.value
            assertEquals("Boulder, Colorado", stored?.placeName)
            assertEquals(listOf(LocalDate(2026, 11, 23)), stored?.days?.map { it.date })
            assertEquals(Condition.Overcast, stored?.days?.single()?.condition)
        }

    @Test
    fun failedRefreshWithNoCacheLeavesItEmpty() =
        runTest {
            val cache = ForecastCache(MapSettings())
            val repository = repositoryFor(cache = cache, weatherBody = null)

            val result = repository.refresh() as RefreshResult.Failure

            assertIs<RefreshFailure.Server>(result.reason)
            assertNull(cache.forecast.value)
        }

    @Test
    fun failedRefreshWithAnExistingCacheLeavesItUntouched() =
        runTest {
            val cache = ForecastCache(MapSettings())
            val existing =
                Forecast(
                    placeName = "Old Place",
                    lat = 1.0,
                    lon = 2.0,
                    fetchedAt = Instant.parse("2026-11-20T00:00:00Z"),
                    days = listOf(DayForecast(LocalDate(2026, 11, 20), Condition.Clear, 20.0, 10.0, 0, 5.0, null, null)),
                )
            cache.write(existing)
            val repository = repositoryFor(cache = cache, weatherBody = null)

            val result = repository.refresh() as RefreshResult.Failure

            assertIs<RefreshFailure.Server>(result.reason)
            assertSame(existing, cache.forecast.value)
        }

    @Test
    fun permissionDeniedNeverTouchesTheCache() =
        runTest {
            val cache = ForecastCache(MapSettings())
            val repository = repositoryFor(locationResult = LocationResult.PermissionDenied(permanently = true), cache = cache)

            val result = repository.refresh() as RefreshResult.Failure

            assertEquals(RefreshFailure.PermissionDenied(permanently = true), result.reason)
            assertNull(cache.forecast.value)
        }

    @Test
    fun locationUnavailableMapsToTheMatchingFailure() =
        runTest {
            val repository = repositoryFor(locationResult = LocationResult.Unavailable)

            val result = repository.refresh() as RefreshResult.Failure

            assertEquals(RefreshFailure.LocationUnavailable, result.reason)
        }

    @Test
    fun locationUnsupportedMapsToTheMatchingFailure() =
        runTest {
            val repository = repositoryFor(locationResult = LocationResult.Unsupported)

            val result = repository.refresh() as RefreshResult.Failure

            assertEquals(RefreshFailure.LocationUnsupported, result.reason)
        }

    @Test
    fun expiredCachedForecastIsNotExposed() =
        runTest {
            val clock = FixedClock(Instant.parse("2026-11-23T12:00:00Z"))
            val today = clock.todayIn(TimeZone.currentSystemDefault())
            val cache = ForecastCache(MapSettings())
            cache.write(
                Forecast(
                    placeName = "Old Place",
                    lat = 1.0,
                    lon = 2.0,
                    fetchedAt = Instant.parse("2026-11-20T00:00:00Z"),
                    days =
                        listOf(
                            DayForecast(today.minus(1, DateTimeUnit.DAY), Condition.Clear, 20.0, 10.0, 0, 5.0, null, null),
                        ),
                ),
            )
            val repository = repositoryFor(cache = cache, clock = clock)

            val emitted = withTimeoutOrNull(100) { repository.forecast.first() }

            assertNull(emitted)
        }

    @Test
    fun cachedForecastEndingTodayIsStillExposed() =
        runTest {
            val clock = FixedClock(Instant.parse("2026-11-23T12:00:00Z"))
            val today = clock.todayIn(TimeZone.currentSystemDefault())
            val cache = ForecastCache(MapSettings())
            val current =
                Forecast(
                    placeName = "Current Place",
                    lat = 1.0,
                    lon = 2.0,
                    fetchedAt = Instant.parse("2026-11-23T00:00:00Z"),
                    days = listOf(DayForecast(today, Condition.Clear, 20.0, 10.0, 0, 5.0, null, null)),
                )
            cache.write(current)
            val repository = repositoryFor(cache = cache, clock = clock)

            val emitted = withTimeoutOrNull(100) { repository.forecast.first() }

            assertNotNull(emitted)
            assertEquals("Current Place", emitted.placeName)
        }

    @Test
    fun geocodeFailureIsNotARefreshFailure() =
        runTest {
            val cache = ForecastCache(MapSettings())
            val repository = repositoryFor(cache = cache, geocodeBody = null)

            val result = repository.refresh()

            assertEquals(RefreshResult.Success, result)
            assertNull(cache.forecast.value?.placeName)
        }
}
