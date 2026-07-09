package com.sypark.finnhub.feature.detail

data class CandleUi(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
)
