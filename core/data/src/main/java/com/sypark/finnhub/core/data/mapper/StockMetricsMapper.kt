package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.domain.model.StockMetrics
import com.sypark.finnhub.core.network.dto.StockMetricResponseDto

fun StockMetricResponseDto.toDomain(symbol: String): StockMetrics = StockMetrics(
    symbol = symbol,
    peRatio = metric.peRatio,
    week52High = metric.week52High,
    week52Low = metric.week52Low,
    epsTTM = metric.epsTTM,
    beta = metric.beta,
)
