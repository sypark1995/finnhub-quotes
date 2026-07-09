package com.sypark.finnhub.feature.detail

data class StockProfileUi(
    val name: String,
    val exchange: String,
    val industry: String,
    val logoUrl: String,
    val marketCapText: String,
    val webUrl: String,
)
