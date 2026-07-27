package com.sypark.finnhub.core.domain.usecase.settings

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.ThemeMode
import com.sypark.finnhub.core.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(mode: ThemeMode): AppResult<Unit> = repository.setThemeMode(mode)
}
