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
    fun `Finnhub's real FX search response (type FX, slash symbol) maps to FOREX`() {
        // Confirmed against the live API: searching "OANDA:EUR_USD" returns
        // {"symbol":"EUR/USD","type":"FX"} -- not the "OANDA:EUR_USD"-style symbol this mapper
        // used to require, which meant no real search result ever classified as FOREX.
        val dto = SearchResultDto("Oanda EUR/USD", "EUR/USD", "EUR/USD", "FX")
        assertEquals(AssetType.FOREX, dto.toDomain().assetType)
    }

    @Test
    fun `a Forex type string also maps to FOREX`() {
        val dto = SearchResultDto("Euro / US Dollar", "EUR/USD", "EUR/USD", "Forex")
        assertEquals(AssetType.FOREX, dto.toDomain().assetType)
    }

    @Test
    fun `a Crypto type maps to CRYPTO`() {
        val dto = SearchResultDto("Bitcoin", "BTCUSDT", "BINANCE:BTCUSDT", "Crypto")
        assertEquals(AssetType.CRYPTO, dto.toDomain().assetType)
    }
}
