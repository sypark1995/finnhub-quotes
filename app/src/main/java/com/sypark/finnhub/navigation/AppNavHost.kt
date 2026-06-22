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
            AppScaffold(navController = navController, currentRoute = Route.Watchlist) {
                PlaceholderScreen(title = "관심종목", modifier = Modifier.padding(it))
            }
        }
        composable<Route.Search> {
            AppScaffold(navController = navController, currentRoute = Route.Search) {
                PlaceholderScreen(title = "검색", modifier = Modifier.padding(it))
            }
        }
        composable<Route.Detail> { entry ->
            val route = entry.toRoute<Route.Detail>()
            AppScaffold(navController = navController, currentRoute = route) {
                PlaceholderScreen(title = "상세 · ${route.symbol}", modifier = Modifier.padding(it))
            }
        }
        composable<Route.Alerts> {
            AppScaffold(navController = navController, currentRoute = Route.Alerts) {
                PlaceholderScreen(title = "가격 알림", modifier = Modifier.padding(it))
            }
        }
        composable<Route.AlertCreate> { entry ->
            val route = entry.toRoute<Route.AlertCreate>()
            AppScaffold(navController = navController, currentRoute = route) {
                PlaceholderScreen(title = "알림 생성", modifier = Modifier.padding(it))
            }
        }
        composable<Route.Settings> {
            AppScaffold(navController = navController, currentRoute = Route.Settings) {
                PlaceholderScreen(title = "설정", modifier = Modifier.padding(it))
            }
        }
    }
}
