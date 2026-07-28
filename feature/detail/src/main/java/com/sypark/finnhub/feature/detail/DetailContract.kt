package com.sypark.finnhub.feature.detail

import com.sypark.finnhub.core.common.UiError

enum class DetailTab { PROFILE, NEWS, PEERS }

data class DetailState(
    val symbol: String = "",
    val quote: QuoteUi? = null,
    val profile: StockProfileUi? = null,
    val metrics: StockMetricsUi? = null,
    val peers: List<String> = emptyList(),
    val news: List<NewsUi> = emptyList(),
    val isInWatchlist: Boolean = false,
    val selectedTab: DetailTab = DetailTab.PROFILE,
    val isLoading: Boolean = true,
    val error: UiError? = null,
)

sealed interface DetailIntent {
    data object Load : DetailIntent
    data class SelectTab(val tab: DetailTab) : DetailIntent
    data object ToggleWatchlist : DetailIntent
    data object CreateAlert : DetailIntent
}

sealed interface DetailEffect {
    data class NavigateToAlertCreate(val symbol: String) : DetailEffect
    data class ShowSnackbar(val message: String) : DetailEffect
}
