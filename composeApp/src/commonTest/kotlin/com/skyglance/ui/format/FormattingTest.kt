package com.skyglance.ui.format

import com.skyglance.domain.TempUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class FormattingTest {
    @Test
    fun zeroCelsiusIsThirtyTwoFahrenheit() {
        assertEquals("32°F", formatTemperature(0.0, TempUnit.Fahrenheit))
    }

    @Test
    fun minusFortyIsEqualInBothUnits() {
        assertEquals("-40°F", formatTemperature(-40.0, TempUnit.Fahrenheit))
        assertEquals("-40°C", formatTemperature(-40.0, TempUnit.Celsius))
    }

    @Test
    fun celsiusPassesThroughUnconverted() {
        assertEquals("18°C", formatTemperature(18.0, TempUnit.Celsius))
    }

    @Test
    fun roundsHalfValuesTowardsPositiveInfinity() {
        // kotlin.math.roundToInt() breaks ties towards positive infinity, not away from zero.
        assertEquals("1°C", formatTemperature(0.5, TempUnit.Celsius))
        assertEquals("0°C", formatTemperature(-0.5, TempUnit.Celsius))
    }

    @Test
    fun negativeTemperaturesKeepTheSign() {
        assertEquals("-5°C", formatTemperature(-5.0, TempUnit.Celsius))
        assertEquals("23°F", formatTemperature(-5.0, TempUnit.Fahrenheit))
    }

    @Test
    fun windSpeedConvertsKmhToMph() {
        assertEquals("10 km/h", formatWindSpeed(10.0, TempUnit.Celsius))
        assertEquals("6 mph", formatWindSpeed(10.0, TempUnit.Fahrenheit))
    }
}
