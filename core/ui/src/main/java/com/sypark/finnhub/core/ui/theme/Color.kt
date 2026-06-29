package com.sypark.finnhub.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Dark theme (default — ui-design.md §2.1 "다크 모드 기본")
// Primary accent modeled after Binance's signature gold/amber brand color.
val DarkPrimary = Color(0xFFF0B90B)
val DarkOnPrimary = Color(0xFF1E1E1E)
val DarkPrimaryContainer = Color(0xFF4A3B0A)
val DarkSecondary = Color(0xFF6BDCAB)
val DarkBackground = Color(0xFF0D1117)
val DarkSurface = Color(0xFF161B22)
val DarkSurfaceVariant = Color(0xFF21262D)
val DarkOutline = Color(0xFF30363D)
val DarkOnBackground = Color(0xFFE6EDF3)
val DarkOnSurfaceVariant = Color(0xFF8B949E)

// Light theme
val LightPrimary = Color(0xFF2563EB)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFD6E4FF)
val LightSecondary = Color(0xFF1F9366)
val LightBackground = Color(0xFFF6F8FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEFF2F5)
val LightOutline = Color(0xFFD0D7DE)
val LightOnBackground = Color(0xFF1F2328)
val LightOnSurfaceVariant = Color(0xFF57606A)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    secondary = DarkSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    secondary = LightSecondary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
)
