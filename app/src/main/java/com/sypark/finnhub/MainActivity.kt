package com.sypark.finnhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rootThemeViewModel: RootThemeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val themeMode by rootThemeViewModel.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                com.sypark.finnhub.core.domain.model.ThemeMode.SYSTEM -> isSystemInDarkTheme()
                com.sypark.finnhub.core.domain.model.ThemeMode.DARK -> true
                com.sypark.finnhub.core.domain.model.ThemeMode.LIGHT -> false
            }
            FinnhubQuotesTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}