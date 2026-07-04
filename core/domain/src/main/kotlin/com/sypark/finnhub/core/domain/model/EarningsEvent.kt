package com.sypark.finnhub.core.domain.model

data class EarningsEvent(
    val symbol: String,
    val date: String,
    val epsEstimate: Double?,
    val epsActual: Double?,
    val revenueEstimate: Double?,
    val revenueActual: Double?
)
