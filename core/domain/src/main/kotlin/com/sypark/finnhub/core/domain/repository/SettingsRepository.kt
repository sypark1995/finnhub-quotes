package com.sypark.finnhub.core.domain.repository

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeRefreshIntervalSeconds(): Flow<Int>
    suspend fun setRefreshIntervalSeconds(seconds: Int): AppResult<Unit>

    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode): AppResult<Unit>
}
