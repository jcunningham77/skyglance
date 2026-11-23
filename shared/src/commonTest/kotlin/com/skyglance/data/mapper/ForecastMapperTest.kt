package com.skyglance.data.mapper

import com.skyglance.data.dto.ForecastResponseDto
import com.skyglance.domain.Condition
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ForecastMapperTest {
    private val json = Json { ignoreUnknownKeys = true }

    // Recorded from a real call to api.open-meteo.com/v1/forecast for Boulder, CO, 2026-11-23.
    private val realResponseJson =
        """
        {"daily":{
            "time":["2026-11-23","2026-11-24","2026-11-25","2026-11-26","2026-11-27","2026-11-28","2026-11-29"],
            "weather_code":[3,45,3,3,3,3,3],
            "temperature_2m_max":[3.7,9.5,9.0,11.4,15.1,17.0,21.1],
            "temperature_2m_min":[-2.4,-3.6,-1.9,0.5,3.6,3.7,6.4],
            "precipitation_probability_max":[3,1,0,0,5,25,10],
            "wind_speed_10m_max":[8.9,10.5,11.2,16.6,35.1,23.9,34.2],
            "sunrise":["2026-11-23T06:55","2026-11-24T06:56","2026-11-25T06:57","2026-11-26T06:58","2026-11-27T06:59","2026-11-28T07:00","2026-11-29T07:01"],
            "sunset":["2026-11-23T16:39","2026-11-24T16:38","2026-11-25T16:38","2026-11-26T16:37","2026-11-27T16:37","2026-11-28T16:37","2026-11-29T16:36"]
        }}
        """.trimIndent()

    private fun parse(responseJson: String): List<com.skyglance.domain.DayForecast> =
        json.decodeFromString(ForecastResponseDto.serializer(), responseJson).toDays()

    @Test
    fun realResponseMapsAllSevenDays() {
        val days = parse(realResponseJson)

        assertEquals(7, days.size)
        assertEquals(LocalDate(2026, 11, 23), days[0].date)
        assertEquals(Condition.Overcast, days[0].condition)
        assertEquals(3.7, days[0].highC)
        assertEquals(-2.4, days[0].lowC)
        assertEquals(3, days[0].precipProbabilityMax)
        assertEquals(8.9, days[0].windMaxKmh)
        assertEquals(Condition.Fog, days[1].condition) // weather_code 45
    }

    @Test
    fun sixDayResponseMapsSixDays() {
        val sixDayJson =
            """
            {"daily":{
                "time":["2026-11-23","2026-11-24","2026-11-25","2026-11-26","2026-11-27","2026-11-28"],
                "weather_code":[3,45,3,3,3,3],
                "temperature_2m_max":[3.7,9.5,9.0,11.4,15.1,17.0],
                "temperature_2m_min":[-2.4,-3.6,-1.9,0.5,3.6,3.7],
                "precipitation_probability_max":[3,1,0,0,5,25],
                "wind_speed_10m_max":[8.9,10.5,11.2,16.6,35.1,23.9],
                "sunrise":["2026-11-23T06:55","2026-11-24T06:56","2026-11-25T06:57","2026-11-26T06:58","2026-11-27T06:59","2026-11-28T07:00"],
                "sunset":["2026-11-23T16:39","2026-11-24T16:38","2026-11-25T16:38","2026-11-26T16:37","2026-11-27T16:37","2026-11-28T16:37"]
            }}
            """.trimIndent()

        assertEquals(6, parse(sixDayJson).size)
    }

    @Test
    fun mismatchedArrayLengthsKeepOnlyTheSharedPrefix() {
        // time/weather_code have 5 entries, but temperature arrays only have 3 — every array is
        // index-aligned with daily.time, so only the first 3 days are usable.
        val mismatchedJson =
            """
            {"daily":{
                "time":["2026-11-23","2026-11-24","2026-11-25","2026-11-26","2026-11-27"],
                "weather_code":[3,45,3,3,3],
                "temperature_2m_max":[3.7,9.5,9.0],
                "temperature_2m_min":[-2.4,-3.6,-1.9],
                "precipitation_probability_max":[3,1,0,0,5],
                "wind_speed_10m_max":[8.9,10.5,11.2,16.6,35.1],
                "sunrise":["2026-11-23T06:55","2026-11-24T06:56","2026-11-25T06:57"],
                "sunset":["2026-11-23T16:39","2026-11-24T16:38","2026-11-25T16:38"]
            }}
            """.trimIndent()

        val days = parse(mismatchedJson)
        assertEquals(3, days.size)
        assertEquals(LocalDate(2026, 11, 25), days.last().date)
    }

    @Test
    fun aNullInARequiredFieldDropsOnlyThatDay() {
        val nullRequiredFieldJson =
            """
            {"daily":{
                "time":["2026-11-23","2026-11-24","2026-11-25"],
                "weather_code":[3,null,3],
                "temperature_2m_max":[3.7,9.5,9.0],
                "temperature_2m_min":[-2.4,-3.6,-1.9],
                "precipitation_probability_max":[3,1,0],
                "wind_speed_10m_max":[8.9,10.5,11.2],
                "sunrise":["2026-11-23T06:55","2026-11-24T06:56","2026-11-25T06:57"],
                "sunset":["2026-11-23T16:39","2026-11-24T16:38","2026-11-25T16:38"]
            }}
            """.trimIndent()

        val days = parse(nullRequiredFieldJson)
        assertEquals(2, days.size)
        assertEquals(listOf(LocalDate(2026, 11, 23), LocalDate(2026, 11, 25)), days.map { it.date })
    }

    @Test
    fun nullsInDetailFieldsKeepTheDayWithNullExtras() {
        val nullDetailFieldsJson =
            """
            {"daily":{
                "time":["2026-11-23"],
                "weather_code":[3],
                "temperature_2m_max":[3.7],
                "temperature_2m_min":[-2.4],
                "precipitation_probability_max":[null],
                "wind_speed_10m_max":[null],
                "sunrise":[null],
                "sunset":[null]
            }}
            """.trimIndent()

        val day = parse(nullDetailFieldsJson).single()
        assertEquals(Condition.Overcast, day.condition)
        assertNull(day.precipProbabilityMax)
        assertNull(day.windMaxKmh)
        assertNull(day.sunrise)
        assertNull(day.sunset)
    }

    @Test
    fun noUsableDaysThrows() {
        val allRequiredFieldsMissingJson =
            """
            {"daily":{
                "time":["2026-11-23","2026-11-24"],
                "weather_code":[null,null],
                "temperature_2m_max":[3.7,9.5],
                "temperature_2m_min":[-2.4,-3.6],
                "precipitation_probability_max":[3,1],
                "wind_speed_10m_max":[8.9,10.5],
                "sunrise":["2026-11-23T06:55","2026-11-24T06:56"],
                "sunset":["2026-11-23T16:39","2026-11-24T16:38"]
            }}
            """.trimIndent()

        assertFailsWith<ForecastMappingException> { parse(allRequiredFieldsMissingJson) }
    }

    @Test
    fun emptyDailyBlockThrows() {
        val emptyJson = """{"daily":{"time":[],"weather_code":[],"temperature_2m_max":[],"temperature_2m_min":[]}}"""

        assertFailsWith<ForecastMappingException> { parse(emptyJson) }
    }
}
