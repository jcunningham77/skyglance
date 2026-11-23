package com.skyglance.data

import com.skyglance.data.dto.ForecastResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WeatherApi(
    private val httpClient: HttpClient,
) {
    suspend fun fetchForecast(
        lat: Double,
        lon: Double,
    ): ForecastResponseDto =
        httpClient
            .get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", lat)
                parameter("longitude", lon)
                parameter(
                    "daily",
                    "weather_code,temperature_2m_max,temperature_2m_min," +
                        "precipitation_probability_max,wind_speed_10m_max,sunrise,sunset",
                )
                parameter("timezone", "auto")
                parameter("forecast_days", 7)
            }.body()
}
