package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.network.dto.EarningsEventDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EarningsEventMapperTest {
    @Test
    fun `toDomain keeps every field including nullable estimates`() {
        val dto = EarningsEventDto(date = "2026-07-15", hour = "amc", epsEstimate = 1.5, epsActual = null, revenueEstimate = null, revenueActual = null, symbol = "AAPL")
        val event = dto.toDomain()
        assertEquals("AAPL", event.symbol)
        assertEquals("amc", event.hour)
        assertEquals(1.5, event.epsEstimate)
    }
}
