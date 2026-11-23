package com.skyglance.data

import com.russhwolf.settings.MapSettings
import com.skyglance.domain.TempUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferencesRepositoryImplTest {
    @Test
    fun defaultsToFahrenheit() =
        runTest {
            val repository = PreferencesRepositoryImpl(MapSettings())

            assertEquals(TempUnit.Fahrenheit, repository.unit.first())
        }

    @Test
    fun unitChoiceSurvivesARestart() =
        runTest {
            val settings = MapSettings()
            PreferencesRepositoryImpl(settings).setUnit(TempUnit.Celsius)

            // A new instance over the same settings simulates the app restarting.
            val afterRestart = PreferencesRepositoryImpl(settings)

            assertEquals(TempUnit.Celsius, afterRestart.unit.first())
        }
}
