package com.skyglance.data

import com.russhwolf.settings.Settings
import com.skyglance.domain.TempUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class PreferencesRepositoryImpl(
    private val settings: Settings,
) : PreferencesRepository {
    private val state = MutableStateFlow(readUnit())

    override val unit: Flow<TempUnit> = state.asStateFlow()

    override suspend fun setUnit(unit: TempUnit) {
        state.value = unit
        settings.putString(KEY, unit.name)
    }

    private fun readUnit(): TempUnit {
        val raw = settings.getStringOrNull(KEY) ?: return TempUnit.Fahrenheit
        return runCatching { TempUnit.valueOf(raw) }.getOrDefault(TempUnit.Fahrenheit)
    }

    private companion object {
        const val KEY = "temp_unit"
    }
}
