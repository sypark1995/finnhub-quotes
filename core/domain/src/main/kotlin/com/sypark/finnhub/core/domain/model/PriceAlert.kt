package com.sypark.finnhub.core.domain.model

import com.sypark.finnhub.core.common.AlertCondition

data class PriceAlert(
    val id: Long,
    val symbol: String,
    val targetPrice: Double,
    val condition: AlertCondition,
    val isEnabled: Boolean,
    val triggeredAt: Long?
)
