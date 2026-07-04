package com.sypark.finnhub.core.websocket

data class TradeMessage(
    val symbol: String,
    val price: Double,
    val volume: Long,
    val timestamp: Long,
)
