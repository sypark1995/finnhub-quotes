package com.sypark.finnhub.feature.watchlist

import com.sypark.finnhub.core.ui.model.ChangeDirection
import com.sypark.finnhub.core.ui.model.UiQuoteSource

data class QuoteUi(
    val price: String,
    val changePercent: String,
    val changeDirection: ChangeDirection,
    val quoteSource: UiQuoteSource,
)
