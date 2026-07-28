package com.sypark.finnhub.feature.watchlist

import com.sypark.finnhub.core.common.UiError
import com.sypark.finnhub.core.domain.model.ConnectionStatus

data class WatchlistState(
    val items: List<WatchlistItemUi> = emptyList(),
    val quotes: Map<String, QuoteUi> = emptyMap(),
    val popularStocks: List<PopularStockUi> = emptyList(),
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: UiError? = null,
)

sealed interface WatchlistIntent {
    data object Load : WatchlistIntent
    data object Refresh : WatchlistIntent
    data class Remove(val symbol: String) : WatchlistIntent
    data class Reorder(val fromIndex: Int, val toIndex: Int) : WatchlistIntent
    data class OpenDetail(val symbol: String, val assetType: com.sypark.finnhub.core.common.AssetType) : WatchlistIntent
    data object OpenSearch : WatchlistIntent
}

sealed interface WatchlistEffect {
    data class NavigateToDetail(val symbol: String, val assetType: com.sypark.finnhub.core.common.AssetType) : WatchlistEffect
    data object NavigateToSearch : WatchlistEffect
    data class ShowSnackbar(val message: String) : WatchlistEffect
}
