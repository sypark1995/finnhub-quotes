package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.datastore.StoredThemeMode
import com.sypark.finnhub.core.domain.model.ThemeMode

fun StoredThemeMode.toDomain(): ThemeMode = when (this) {
    StoredThemeMode.SYSTEM -> ThemeMode.SYSTEM
    StoredThemeMode.DARK -> ThemeMode.DARK
    StoredThemeMode.LIGHT -> ThemeMode.LIGHT
}

fun ThemeMode.toStored(): StoredThemeMode = when (this) {
    ThemeMode.SYSTEM -> StoredThemeMode.SYSTEM
    ThemeMode.DARK -> StoredThemeMode.DARK
    ThemeMode.LIGHT -> StoredThemeMode.LIGHT
}
