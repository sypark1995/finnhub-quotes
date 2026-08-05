package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.database.entity.EarningsCacheEntity
import com.sypark.finnhub.core.domain.model.EarningsEvent
import com.sypark.finnhub.core.network.dto.EarningsEventDto

fun EarningsEventDto.toDomain(): EarningsEvent = EarningsEvent(
    symbol = symbol,
    date = date,
    hour = hour,
    epsEstimate = epsEstimate,
    epsActual = epsActual,
    revenueEstimate = revenueEstimate,
    revenueActual = revenueActual,
)

fun EarningsCacheEntity.toDomain(): EarningsEvent = EarningsEvent(
    symbol = symbol,
    date = date,
    hour = hour,
    epsEstimate = epsEstimate,
    epsActual = epsActual,
    revenueEstimate = revenueEstimate,
    revenueActual = revenueActual,
)

fun EarningsEvent.toCacheEntity(fetchedAt: Long): EarningsCacheEntity = EarningsCacheEntity(
    symbol = symbol,
    date = date,
    hour = hour,
    epsEstimate = epsEstimate,
    epsActual = epsActual,
    revenueEstimate = revenueEstimate,
    revenueActual = revenueActual,
    fetchedAt = fetchedAt,
)
