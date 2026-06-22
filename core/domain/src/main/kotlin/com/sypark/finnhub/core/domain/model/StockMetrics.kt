package com.sypark.finnhub.core.domain.model

data class StockMetrics(
    val symbol: String,
    val peRatio: Double?,
    val week52High: Double?,
    val week52Low: Double?,
    val epsTTM: Double?,
    val beta: Double?
)
