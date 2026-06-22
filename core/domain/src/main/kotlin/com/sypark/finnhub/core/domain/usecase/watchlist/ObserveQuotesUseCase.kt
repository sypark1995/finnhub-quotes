package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.repository.MarketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveQuotesUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    operator fun invoke(symbols: Set<String>): Flow<Map<String, Quote>> = repository.observeQuotes(symbols)
}
