package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.database.entity.QuoteCacheEntity
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.network.dto.QuoteDto
import com.sypark.finnhub.core.websocket.TradeMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QuoteMapperTest {

    @Test
    fun `QuoteDto toDomain tags the quote as REST and converts seconds to millis`() {
        val dto = QuoteDto(c = 198.5, d = 2.3, dp = 1.17, h = 199.1, l = 196.8, o = 197.2, pc = 196.2, t = 1_720_000_000)

        val quote = dto.toDomain(symbol = "AAPL")

        assertEquals("AAPL", quote.symbol)
        assertEquals(198.5, quote.price)
        assertEquals(QuoteSource.REST, quote.source)
        assertEquals(1_720_000_000_000L, quote.timestamp)
    }

    @Test
    fun `QuoteCacheEntity toDomain tags the quote as CACHE`() {
        val entity = QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = 1L)
        assertEquals(QuoteSource.CACHE, entity.toDomain().source)
    }

    @Test
    fun `TradeMessage toDomain derives change and changePercent from the previous quote's previousClose`() {
        val previous = QuoteDto(c = 196.2, d = 0.0, dp = 0.0, h = 196.2, l = 196.2, o = 196.2, pc = 196.2, t = 0).toDomain("AAPL")
        val trade = TradeMessage(symbol = "AAPL", price = 198.5, volume = 10, timestamp = 2_000L)

        val quote = trade.toDomain(previous)

        assertEquals(QuoteSource.WEBSOCKET, quote.source)
        assertEquals(196.2, quote.previousClose)
        assertEquals(2.3, quote.change, 0.001)
        assertEquals((2.3 / 196.2) * 100, quote.changePercent, 0.001)
    }

    @Test
    fun `TradeMessage toDomain with no previous quote treats price as its own previousClose`() {
        val trade = TradeMessage(symbol = "AAPL", price = 198.5, volume = 10, timestamp = 2_000L)
        val quote = trade.toDomain(previous = null)
        assertEquals(0.0, quote.change)
        assertEquals(0.0, quote.changePercent)
    }
}
