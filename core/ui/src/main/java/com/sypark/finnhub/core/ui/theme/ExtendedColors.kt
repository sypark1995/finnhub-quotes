package com.sypark.finnhub.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    val gain: Color,
    val onGain: Color,
    val gainContainer: Color,
    val loss: Color,
    val onLoss: Color,
    val lossContainer: Color,
    val neutral: Color,
    val live: Color,
    val delayed: Color,
    val offline: Color,
)

val DarkExtendedColors = ExtendedColors(
    gain = Color(0xFF3DD68C),
    onGain = Color(0xFF0D1117),
    gainContainer = Color(0xFF1A3D2E),
    loss = Color(0xFFF85149),
    onLoss = Color(0xFFFFFFFF),
    lossContainer = Color(0xFF3D1A1A),
    neutral = Color(0xFF8B949E),
    live = Color(0xFF3DD68C),
    delayed = Color(0xFFD29922),
    offline = Color(0xFF8B949E),
)

val LightExtendedColors = ExtendedColors(
    gain = Color(0xFF1A7F4E),
    onGain = Color(0xFFFFFFFF),
    gainContainer = Color(0xFFD6F5E3),
    loss = Color(0xFFCF222E),
    onLoss = Color(0xFFFFFFFF),
    lossContainer = Color(0xFFFCE4E4),
    neutral = Color(0xFF57606A),
    live = Color(0xFF1A7F4E),
    delayed = Color(0xFF9A6700),
    offline = Color(0xFF57606A),
)

val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }

object AppTheme {
    val extended: ExtendedColors
        @Composable get() = LocalExtendedColors.current
}
