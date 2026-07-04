package com.sypark.finnhub.core.domain.model

data class Quote(
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val previousClose: Double,
    val timestamp: Long,
    val source: QuoteSource
)

enum class QuoteSource { WEBSOCKET, REST, CACHE }
