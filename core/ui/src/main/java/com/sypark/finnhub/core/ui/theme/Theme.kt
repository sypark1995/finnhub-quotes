package com.sypark.finnhub.core.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.os.Build

@Composable
fun FinnhubQuotesTheme(
    // ui-design.md §2.1 "다크 모드 기본" — Binance-style exchange apps default to dark
    // regardless of system setting; users can still override via app-level settings later.
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val extended = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extended) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FinnhubQuotesThemePreview() {
    FinnhubQuotesTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Display Large", style = MaterialTheme.typography.displayLarge)
            Text("Body Large", style = MaterialTheme.typography.bodyLarge)
            Text("$123.45", style = PriceTypographyLarge)
        }
    }
}
