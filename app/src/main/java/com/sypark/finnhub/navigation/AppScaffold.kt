package com.sypark.finnhub.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
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
                        if (config.showEarningsAction) {
                            IconButton(onClick = { navController.navigate(Route.Earnings) }) {
                                Icon(Icons.Filled.Event, contentDescription = "실적 캘린더")
                            }
                        }
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
                val badgeViewModel: AlertBadgeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val alertBadgeCount by badgeViewModel.enabledAlertCount.collectAsStateWithLifecycle()
                AppBottomBar(
                    selectedTab = config.bottomNavTab,
                    alertBadgeCount = alertBadgeCount,
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
    // saveState/restoreState combined with popUpTo(startDestination) silently no-ops navigation
    // back to the graph's type-safe-route start destination (confirmed via currentBackStackEntry
    // logging: navigate() ran without throwing but left the back stack completely unchanged).
    // Dropping save/restore state avoids the bug; each tab simply reloads on re-entry.
    navigate(route) {
        popUpTo(graph.findStartDestination().id)
        launchSingleTop = true
    }
}
