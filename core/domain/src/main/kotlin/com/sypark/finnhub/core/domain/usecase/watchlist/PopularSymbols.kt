package com.sypark.finnhub.core.domain.usecase.watchlist

/**
 * Finnhub's free tier has no market-wide "most active"/volume-ranking endpoint,
 * so the watchlist's "인기 종목" section shows this curated list of well-known
 * large-cap tickers instead, ranked client-side by live |changePercent|.
 */
object PopularSymbols {
    data class Entry(val symbol: String, val displayName: String)

    val ENTRIES: List<Entry> = listOf(
        Entry("AAPL", "Apple"),
        Entry("MSFT", "Microsoft"),
        Entry("NVDA", "NVIDIA"),
        Entry("GOOGL", "Alphabet"),
        Entry("AMZN", "Amazon"),
        Entry("META", "Meta"),
        Entry("TSLA", "Tesla"),
        Entry("NFLX", "Netflix"),
        Entry("AMD", "AMD"),
        Entry("AVGO", "Broadcom"),
        Entry("INTC", "Intel"),
        Entry("QCOM", "Qualcomm"),
        Entry("ORCL", "Oracle"),
        Entry("CRM", "Salesforce"),
        Entry("ADBE", "Adobe"),
        Entry("PYPL", "PayPal"),
        Entry("DIS", "Disney"),
        Entry("UBER", "Uber"),
        Entry("COIN", "Coinbase"),
        Entry("PLTR", "Palantir"),
    )

    val SYMBOLS: Set<String> = ENTRIES.map { it.symbol }.toSet()
}
