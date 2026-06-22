package com.sypark.finnhub.core.domain.model

data class StockProfile(
    val symbol: String,
    val name: String,
    val exchange: String,
    val industry: String,
    val logoUrl: String,
    val marketCapitalization: Double,
    val webUrl: String,
    val currency: String
)
