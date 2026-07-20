package com.sypark.finnhub.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute

@Composable
fun AppNavHost(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute: Route = backStackEntry?.let { entry ->
        when (entry.destination.route?.substringBefore("?")?.substringBefore("/")) {
            else -> Route.Watchlist // resolved precisely inside each composable() below via toRoute(); this
            // top-level default only feeds AppScaffold's chrome before the first destination resolves.
        }
    } ?: Route.Watchlist

    NavHost(navController = navController, startDestination = Route.Watchlist) {
        composable<Route.Watchlist> {
            AppScaffold(navController = navController, currentRoute = Route.Watchlist) { padding ->
                com.sypark.finnhub.feature.watchlist.WatchlistRoute(
                    onNavigateToDetail = { symbol -> navController.navigate(Route.Detail(symbol)) },
                    onNavigateToSearch = { navController.navigate(Route.Search) },
                    modifier = androidx.compose.ui.Modifier.padding(padding),
                )
            }
        }
        composable<Route.Search> {
            AppScaffold(navController = navController, currentRoute = Route.Search) { padding ->
                com.sypark.finnhub.feature.search.SearchRoute(
                    onNavigateToDetail = { symbol -> navController.navigate(Route.Detail(symbol)) },
                    modifier = androidx.compose.ui.Modifier.padding(padding),
                )
            }
        }
        composable<Route.Detail> { entry ->
            val route = entry.toRoute<Route.Detail>()
            AppScaffold(navController = navController, currentRoute = route) { padding ->
                com.sypark.finnhub.feature.detail.DetailRoute(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAlertCreate = { symbol -> navController.navigate(Route.AlertCreate(symbol)) },
                    onNavigateToPeerDetail = { symbol -> navController.navigate(Route.Detail(symbol)) },
                    modifier = androidx.compose.ui.Modifier.padding(padding),
                )
            }
        }
        composable<Route.Alerts> {
            AppScaffold(navController = navController, currentRoute = Route.Alerts) { padding ->
                com.sypark.finnhub.feature.alert.AlertListRoute(
                    onNavigateToCreate = { navController.navigate(Route.AlertCreate()) },
                    modifier = androidx.compose.ui.Modifier.padding(padding),
                )
            }
        }
        composable<Route.AlertCreate> { entry ->
            val route = entry.toRoute<Route.AlertCreate>()
            AppScaffold(navController = navController, currentRoute = route) { _ ->
                com.sypark.finnhub.feature.alert.AlertCreateRoute(onDismiss = { navController.popBackStack() })
            }
        }
        composable<Route.Settings> {
            AppScaffold(navController = navController, currentRoute = Route.Settings) { padding ->
                com.sypark.finnhub.settings.SettingsRoute(modifier = androidx.compose.ui.Modifier.padding(padding))
            }
        }
    }
}
