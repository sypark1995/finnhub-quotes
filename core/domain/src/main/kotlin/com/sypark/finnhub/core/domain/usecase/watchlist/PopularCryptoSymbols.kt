package com.sypark.finnhub.core.domain.usecase.watchlist

/**
 * Curated large-cap crypto pairs, mirroring PopularSymbols but for AssetType.CRYPTO. Unlike
 * FOREX, Finnhub's free tier genuinely serves live quotes for these BINANCE:xxxUSDT symbols
 * (confirmed via direct API calls -- real non-zero price/change data), so this is worth
 * surfacing the same way the stock watchlist home screen does.
 */
object PopularCryptoSymbols {
    data class Entry(val symbol: String, val displayName: String)

    val ENTRIES: List<Entry> = listOf(
        Entry("BINANCE:BTCUSDT", "Bitcoin"),
        Entry("BINANCE:ETHUSDT", "Ethereum"),
        Entry("BINANCE:SOLUSDT", "Solana"),
        Entry("BINANCE:XRPUSDT", "XRP"),
        Entry("BINANCE:DOGEUSDT", "Dogecoin"),
        Entry("BINANCE:ADAUSDT", "Cardano"),
        Entry("BINANCE:AVAXUSDT", "Avalanche"),
        Entry("BINANCE:LINKUSDT", "Chainlink"),
        Entry("BINANCE:LTCUSDT", "Litecoin"),
    )

    val SYMBOLS: Set<String> = ENTRIES.map { it.symbol }.toSet()
}
