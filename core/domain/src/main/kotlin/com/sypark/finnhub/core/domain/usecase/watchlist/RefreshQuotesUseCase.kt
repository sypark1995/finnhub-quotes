package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.domain.repository.MarketRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RefreshQuotesUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    suspend operator fun invoke(symbols: Set<String>) = coroutineScope {
        symbols.forEach { symbol -> launch { repository.getQuote(symbol) } }
    }
}
