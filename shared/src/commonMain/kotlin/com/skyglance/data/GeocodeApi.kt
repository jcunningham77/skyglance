package com.skyglance.data

import com.skyglance.data.dto.GeocodeResponseDto
import com.skyglance.data.dto.toPlaceName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CancellationException

class GeocodeApi(
    private val httpClient: HttpClient,
) {
    /** Best-effort place name, or null if the lookup fails for any reason. A geocode failure is
     * never a refresh failure — the caller falls back to coordinates — so this never throws. */
    suspend fun reverseGeocode(
        lat: Double,
        lon: Double,
    ): String? =
        try {
            httpClient
                .get("https://api-bdc.io/data/reverse-geocode-client") {
                    parameter("latitude", lat)
                    parameter("longitude", lon)
                    parameter("localityLanguage", "en")
                }.body<GeocodeResponseDto>()
                .toPlaceName()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
}
