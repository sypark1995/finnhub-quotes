package com.sypark.finnhub.settings

import com.sypark.finnhub.core.domain.model.ThemeMode

enum class ApiStatus { OK, DEGRADED, UNKNOWN }

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val apiStatus: ApiStatus = ApiStatus.UNKNOWN,
    val versionName: String = "1.0.0",
)

sealed interface SettingsIntent {
    data class ThemeChanged(val mode: ThemeMode) : SettingsIntent
}
