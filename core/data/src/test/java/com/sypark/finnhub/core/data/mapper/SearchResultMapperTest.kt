package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.network.dto.SearchResultDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchResultMapperTest {

    @Test
    fun `a plain symbol with a stock type maps to STOCK`() {
        val dto = SearchResultDto("APPLE INC", "AAPL", "AAPL", "Common Stock")
        assertEquals(AssetType.STOCK, dto.toDomain().assetType)
    }

    @Test
    fun `an EXCHANGE_BASE_QUOTE symbol maps to FOREX`() {
        val dto = SearchResultDto("Euro / US Dollar", "EUR/USD", "OANDA:EUR_USD", "Forex")
        assertEquals(AssetType.FOREX, dto.toDomain().assetType)
    }

    @Test
    fun `a Crypto type maps to CRYPTO`() {
        val dto = SearchResultDto("Bitcoin", "BTCUSDT", "BINANCE:BTCUSDT", "Crypto")
        assertEquals(AssetType.CRYPTO, dto.toDomain().assetType)
    }
}
