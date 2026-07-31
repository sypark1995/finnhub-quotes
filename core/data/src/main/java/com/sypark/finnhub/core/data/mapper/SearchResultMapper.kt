package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.network.dto.SearchResultDto

fun SearchResultDto.toDomain(): SearchResult {
    // Finnhub's real /search response uses type="FX" and symbol="EUR/USD" (confirmed via direct
    // API call) -- not the "OANDA:EUR_USD"-style symbol used for WebSocket subscriptions/quotes.
    // A symbol-format heuristic never matches real search results, so every forex result silently
    // fell through to STOCK and never appeared under the 환율 filter. Match on the DTO's own type
    // field instead, which Finnhub already tells us directly.
    val assetType = when {
        type.contains("Crypto", ignoreCase = true) -> AssetType.CRYPTO
        type.equals("FX", ignoreCase = true) || type.contains("Forex", ignoreCase = true) -> AssetType.FOREX
        else -> AssetType.STOCK
    }
    return SearchResult(symbol = symbol, description = description, assetType = assetType)
}
