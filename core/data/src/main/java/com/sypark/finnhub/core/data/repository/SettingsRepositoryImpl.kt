package com.sypark.finnhub.core.data.repository

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.UiError
import com.sypark.finnhub.core.data.mapper.toDomain
import com.sypark.finnhub.core.data.mapper.toStored
import com.sypark.finnhub.core.datastore.UserPreferencesDataSource
import com.sypark.finnhub.core.domain.model.ThemeMode
import com.sypark.finnhub.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : SettingsRepository {

    override fun observeRefreshIntervalSeconds(): Flow<Int> = dataSource.refreshIntervalSeconds

    override suspend fun setRefreshIntervalSeconds(seconds: Int): AppResult<Unit> =
        runCatching { dataSource.setRefreshIntervalSeconds(seconds) }
            .fold({ AppResult.Success(Unit) }, { AppResult.Error(UiError.Unknown(it.message ?: "failed")) })

    override fun observeThemeMode(): Flow<ThemeMode> = dataSource.themeMode.map { it.toDomain() }

    override suspend fun setThemeMode(mode: ThemeMode): AppResult<Unit> =
        runCatching { dataSource.setThemeMode(mode.toStored()) }
            .fold({ AppResult.Success(Unit) }, { AppResult.Error(UiError.Unknown(it.message ?: "failed")) })
}
