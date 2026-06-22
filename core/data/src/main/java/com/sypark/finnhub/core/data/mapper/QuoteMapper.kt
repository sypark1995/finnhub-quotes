package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.database.entity.QuoteCacheEntity
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.network.dto.QuoteDto
import com.sypark.finnhub.core.websocket.TradeMessage

fun QuoteDto.toDomain(symbol: String): Quote = Quote(
    symbol = symbol,
    price = c,
    change = d,
    changePercent = dp,
    high = h,
    low = l,
    open = o,
    previousClose = pc,
    timestamp = t * 1000,
    source = QuoteSource.REST,
)

fun QuoteCacheEntity.toDomain(): Quote = Quote(
    symbol = symbol,
    price = price,
    change = change,
    changePercent = changePercent,
    high = high,
    low = low,
    open = open,
    previousClose = previousClose,
    timestamp = updatedAt,
    source = QuoteSource.CACHE,
)

fun Quote.toCacheEntity(): QuoteCacheEntity = QuoteCacheEntity(
    symbol = symbol,
    price = price,
    change = change,
    changePercent = changePercent,
    high = high,
    low = low,
    open = open,
    previousClose = previousClose,
    updatedAt = timestamp,
)

fun TradeMessage.toDomain(previous: Quote?): Quote {
    val previousClose = previous?.previousClose ?: price
    val change = price - previousClose
    val changePercent = if (previousClose != 0.0) (change / previousClose) * 100 else 0.0
    return Quote(
        symbol = symbol,
        price = price,
        change = change,
        changePercent = changePercent,
        high = maxOf(previous?.high ?: price, price),
        low = minOf(previous?.low ?: price, price),
        open = previous?.open ?: price,
        previousClose = previousClose,
        timestamp = timestamp,
        source = QuoteSource.WEBSOCKET,
    )
}
