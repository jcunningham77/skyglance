package com.skyglance.data

import com.russhwolf.settings.Settings
import com.skyglance.domain.Forecast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * One cached [Forecast] record, seeded from [settings] at construction so the first value is
 * available immediately and synchronously with launch — before any network call. A schema version
 * is stored alongside the forecast so a future shape change discards old data instead of crashing.
 */
class ForecastCache(
    private val settings: Settings,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val state = MutableStateFlow(readFromSettings())

    val forecast: StateFlow<Forecast?> = state.asStateFlow()

    fun write(forecast: Forecast) {
        state.value = forecast
        settings.putString(KEY, json.encodeToString(CachedForecast.serializer(), CachedForecast(SCHEMA_VERSION, forecast)))
    }

    private fun readFromSettings(): Forecast? {
        val raw = settings.getStringOrNull(KEY) ?: return null
        val cached =
            try {
                json.decodeFromString(CachedForecast.serializer(), raw)
            } catch (_: SerializationException) {
                return null
            }
        return cached.forecast.takeIf { cached.schemaVersion == SCHEMA_VERSION }
    }

    private companion object {
        const val KEY = "forecast_cache"
        const val SCHEMA_VERSION = 1
    }
}

@Serializable
private data class CachedForecast(
    val schemaVersion: Int,
    val forecast: Forecast,
)
