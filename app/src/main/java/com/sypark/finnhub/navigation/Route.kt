package com.sypark.finnhub.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Watchlist : Route
    @Serializable data object Search : Route
    @Serializable data class Detail(val symbol: String) : Route
    @Serializable data object Alerts : Route
    @Serializable data class AlertCreate(val symbol: String? = null) : Route
    @Serializable data object Settings : Route
}
