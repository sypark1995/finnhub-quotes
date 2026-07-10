package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.database.entity.PriceAlertEntity
import com.sypark.finnhub.core.domain.model.PriceAlert

fun PriceAlertEntity.toDomain(): PriceAlert = PriceAlert(
    id = id,
    symbol = symbol,
    targetPrice = targetPrice,
    condition = condition,
    isEnabled = isEnabled,
    triggeredAt = triggeredAt,
)

fun PriceAlert.toEntity(createdAt: Long): PriceAlertEntity = PriceAlertEntity(
    id = id,
    symbol = symbol,
    targetPrice = targetPrice,
    condition = condition,
    isEnabled = isEnabled,
    triggeredAt = triggeredAt,
    createdAt = createdAt,
)
