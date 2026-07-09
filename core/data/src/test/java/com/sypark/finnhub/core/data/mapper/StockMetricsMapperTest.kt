package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.network.dto.StockMetricDto
import com.sypark.finnhub.core.network.dto.StockMetricResponseDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StockMetricsMapperTest {
    @Test
    fun `toDomain unwraps the nested metric object and carries the given symbol`() {
        val dto = StockMetricResponseDto(StockMetricDto(peRatio = 32.5, week52High = 199.62, week52Low = 164.08, epsTTM = 6.1, beta = 1.2))
        val metrics = dto.toDomain(symbol = "AAPL")
        assertEquals("AAPL", metrics.symbol)
        assertEquals(32.5, metrics.peRatio)
        assertEquals(1.2, metrics.beta)
    }
}
