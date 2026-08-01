package com.sypark.finnhub.feature.earnings

data class EarningsEventUi(
    val symbol: String,
    val displayName: String,
    val dateText: String,
    val timingText: String,
    val epsEstimateText: String,
)
