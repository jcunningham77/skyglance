package com.skyglance.data

import com.skyglance.domain.TempUnit
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val unit: Flow<TempUnit>

    suspend fun setUnit(unit: TempUnit)
}
