package com.sypark.finnhub.feature.detail

import com.sypark.finnhub.core.ui.model.ChangeDirection
import com.sypark.finnhub.core.ui.model.UiQuoteSource

data class QuoteUi(
    val price: String,
    val change: String,
    val changePercent: String,
    val changeDirection: ChangeDirection,
    val high: String,
    val low: String,
    val open: String,
    val quoteSource: UiQuoteSource,
)
