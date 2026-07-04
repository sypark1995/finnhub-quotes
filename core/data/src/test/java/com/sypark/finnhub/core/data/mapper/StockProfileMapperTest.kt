package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.network.dto.StockProfileDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StockProfileMapperTest {
    @Test
    fun `toDomain carries the given symbol and every DTO field`() {
        val dto = StockProfileDto(
            name = "Apple Inc", exchange = "NASDAQ", finnhubIndustry = "Technology",
            logo = "https://x/logo.png", marketCapitalization = 3_010_000.0, weburl = "https://apple.com", currency = "USD",
        )
        val profile = dto.toDomain(symbol = "AAPL")
        assertEquals("AAPL", profile.symbol)
        assertEquals("Technology", profile.industry)
        assertEquals("https://x/logo.png", profile.logoUrl)
    }
}
