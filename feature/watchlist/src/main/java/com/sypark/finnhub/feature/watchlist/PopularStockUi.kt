package com.sypark.finnhub.feature.watchlist

import com.sypark.finnhub.core.ui.model.ChangeDirection

data class PopularStockUi(
    val symbol: String,
    val displayName: String,
    val price: String,
    val changePercent: String,
    val changeDirection: ChangeDirection,
    val changePercentValue: Double,
)
