package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.database.entity.CandleCacheEntity
import com.sypark.finnhub.core.domain.model.Candle
import com.sypark.finnhub.core.network.dto.CandleResponseDto

fun CandleResponseDto.toDomain(): List<Candle> {
    if (s != "ok") return emptyList()
    return t.indices.map { i ->
        Candle(timestamp = t[i], open = o[i], high = h[i], low = l[i], close = c[i], volume = v[i])
    }
}

fun CandleCacheEntity.toDomain(): Candle = Candle(timestamp, open, high, low, close, volume)

fun Candle.toCacheEntity(symbol: String, resolution: String, fetchedAt: Long): CandleCacheEntity =
    CandleCacheEntity(symbol = symbol, resolution = resolution, timestamp = timestamp, open = open, high = high, low = low, close = close, volume = volume, fetchedAt = fetchedAt)
