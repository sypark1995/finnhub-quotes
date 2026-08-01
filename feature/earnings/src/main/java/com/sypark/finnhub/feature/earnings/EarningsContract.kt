package com.sypark.finnhub.feature.earnings

data class EarningsState(
    val events: List<EarningsEventUi> = emptyList(),
    val isLoading: Boolean = true,
)

sealed interface EarningsIntent {
    data object Load : EarningsIntent
    data class OpenDetail(val symbol: String) : EarningsIntent
}

sealed interface EarningsEffect {
    data class NavigateToDetail(val symbol: String) : EarningsEffect
}
