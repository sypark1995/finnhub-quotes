package com.sypark.finnhub.feature.search

import com.sypark.finnhub.core.common.UiError

data class SearchState(
    val query: String = "",
    val results: List<SearchResultUi> = emptyList(),
    val watchlistSymbols: Set<String> = emptySet(),
    val selectedFilter: AssetTypeFilter = AssetTypeFilter.ALL,
    val isSearching: Boolean = false,
    val error: UiError? = null,
)

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data class FilterChanged(val filter: AssetTypeFilter) : SearchIntent
    data class AddToWatchlist(val result: SearchResultUi) : SearchIntent
    data class RemoveFromWatchlist(val symbol: String) : SearchIntent
    data class OpenDetail(val symbol: String) : SearchIntent
}

sealed interface SearchEffect {
    data class NavigateToDetail(val symbol: String) : SearchEffect
}
