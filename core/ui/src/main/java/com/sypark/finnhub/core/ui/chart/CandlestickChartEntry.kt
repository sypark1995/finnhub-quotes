package com.sypark.finnhub.core.ui.chart

data class CandlestickChartEntry(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
)
