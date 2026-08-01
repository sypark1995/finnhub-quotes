package com.sypark.finnhub.navigation

import com.sypark.finnhub.core.ui.component.BottomNavTab

data class RouteUiConfig(
    val showBottomBar: Boolean,
    val useDefaultTopBar: Boolean,
    val topBarTitle: String,
    val showBackButton: Boolean,
    val showSearchAction: Boolean,
    val showEarningsAction: Boolean = false,
    val bottomNavTab: BottomNavTab?,
)

fun routeUiConfig(route: Route): RouteUiConfig = when (route) {
    is Route.Watchlist -> RouteUiConfig(
        showBottomBar = true, useDefaultTopBar = true, topBarTitle = "관심종목",
        showBackButton = false, showSearchAction = true, showEarningsAction = true, bottomNavTab = BottomNavTab.HOME,
    )
    is Route.Search -> RouteUiConfig(
        showBottomBar = true, useDefaultTopBar = true, topBarTitle = "검색",
        showBackButton = true, showSearchAction = false, bottomNavTab = BottomNavTab.SEARCH,
    )
    is Route.Detail -> RouteUiConfig(
        // Detail renders its own TopAppBar with ★/🔔 actions (Task 46) — AppScaffold skips its default bar.
        showBottomBar = false, useDefaultTopBar = false, topBarTitle = route.symbol,
        showBackButton = true, showSearchAction = false, bottomNavTab = null,
    )
    is Route.Alerts -> RouteUiConfig(
        showBottomBar = true, useDefaultTopBar = true, topBarTitle = "가격 알림",
        showBackButton = false, showSearchAction = false, bottomNavTab = BottomNavTab.ALERTS,
    )
    is Route.AlertCreate -> RouteUiConfig(
        // Bottom sheet route — no Scaffold top bar, no bottom nav.
        showBottomBar = false, useDefaultTopBar = false, topBarTitle = "",
        showBackButton = false, showSearchAction = false, bottomNavTab = null,
    )
    is Route.Settings -> RouteUiConfig(
        showBottomBar = true, useDefaultTopBar = true, topBarTitle = "설정",
        showBackButton = false, showSearchAction = false, bottomNavTab = BottomNavTab.SETTINGS,
    )
    is Route.Earnings -> RouteUiConfig(
        showBottomBar = false, useDefaultTopBar = true, topBarTitle = "실적 캘린더",
        showBackButton = true, showSearchAction = false, bottomNavTab = null,
    )
}
