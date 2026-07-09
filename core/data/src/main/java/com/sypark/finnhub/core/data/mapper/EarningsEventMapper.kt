package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.domain.model.EarningsEvent
import com.sypark.finnhub.core.network.dto.EarningsEventDto

fun EarningsEventDto.toDomain(): EarningsEvent = EarningsEvent(
    symbol = symbol,
    date = date,
    epsEstimate = epsEstimate,
    epsActual = epsActual,
    revenueEstimate = revenueEstimate,
    revenueActual = revenueActual,
)
