package com.sypark.finnhub.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.sypark.finnhub.core.ui.R.array.com_google_android_gms_fonts_certs,
)

private val interGoogleFont = GoogleFont("Inter")
private val jetBrainsMonoGoogleFont = GoogleFont("JetBrains Mono")

// Falls back to FontFamily.Default automatically if the Google Fonts provider
// is unreachable (no Play Services, offline first launch, cert rotation) —
// documented behavior of downloadable Font, not a custom fallback branch.
val InterFontFamily = FontFamily(
    Font(googleFont = interGoogleFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = interGoogleFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = interGoogleFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = interGoogleFont, fontProvider = fontProvider, weight = FontWeight.Bold),
)

val JetBrainsMonoFontFamily = FontFamily(
    Font(googleFont = jetBrainsMonoGoogleFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = jetBrainsMonoGoogleFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = jetBrainsMonoGoogleFont, fontProvider = fontProvider, weight = FontWeight.Bold),
)
