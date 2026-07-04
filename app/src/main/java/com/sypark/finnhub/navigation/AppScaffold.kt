package com.sypark.finnhub.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sypark.finnhub.core.ui.component.AppBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController,
    currentRoute: Route,
    content: @Composable (PaddingValues) -> Unit,
) {
    val config = routeUiConfig(currentRoute)

    Scaffold(
        topBar = {
            if (config.useDefaultTopBar) {
                TopAppBar(
                    title = { Text(config.topBarTitle) },
                    navigationIcon = {
                        if (config.showBackButton) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                            }
                        }
                    },
                    actions = {
                        if (config.showSearchAction) {
                            IconButton(onClick = { navController.navigate(Route.Search) }) {
                                Icon(Icons.Filled.Search, contentDescription = "검색")
                            }
                        }
                    },
                )
            }
        },
        bottomBar = {
            if (config.showBottomBar && config.bottomNavTab != null) {
                AppBottomBar(
                    selectedTab = config.bottomNavTab,
                    alertBadgeCount = 0, // Task 59 replaces this with live enabled-alert count.
                    onTabSelected = { tab -> navController.navigateToTab(tab) },
                )
            }
        },
        content = content,
    )
}

private fun NavHostController.navigateToTab(tab: com.sypark.finnhub.core.ui.component.BottomNavTab) {
    val route: Route = when (tab) {
        com.sypark.finnhub.core.ui.component.BottomNavTab.HOME -> Route.Watchlist
        com.sypark.finnhub.core.ui.component.BottomNavTab.SEARCH -> Route.Search
        com.sypark.finnhub.core.ui.component.BottomNavTab.ALERTS -> Route.Alerts
        com.sypark.finnhub.core.ui.component.BottomNavTab.SETTINGS -> Route.Settings
    }
    navigate(route) {
        popUpTo(Route.Watchlist) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
