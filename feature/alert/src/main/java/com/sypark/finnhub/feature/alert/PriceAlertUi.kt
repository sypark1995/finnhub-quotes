package com.sypark.finnhub.feature.alert

data class PriceAlertUi(
    val id: Long,
    val symbol: String,
    val conditionText: String,
    val isEnabled: Boolean,
    val triggeredText: String?,
)
