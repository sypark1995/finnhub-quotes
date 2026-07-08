package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.network.dto.CandleResponseDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CandleMapperTest {

    @Test
    fun `toDomain zips the parallel OHLCV arrays into Candle list when s is ok`() {
        val dto = CandleResponseDto(c = listOf(198.5), h = listOf(199.1), l = listOf(196.8), o = listOf(197.2), s = "ok", t = listOf(1L), v = listOf(1000L))
        val candles = dto.toDomain()
        assertEquals(1, candles.size)
        assertEquals(198.5, candles.single().close)
    }

    @Test
    fun `toDomain returns an empty list when s is not ok`() {
        val dto = CandleResponseDto(s = "no_data")
        assertTrue(dto.toDomain().isEmpty())
    }
}
