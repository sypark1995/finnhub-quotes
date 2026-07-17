package com.sypark.finnhub.core.domain.usecase.settings

import com.sypark.finnhub.core.domain.model.ThemeMode
import com.sypark.finnhub.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveThemeModeUseCase @Inject constructor(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<ThemeMode> = repository.observeThemeMode()
}
