package com.sypark.finnhub.core.domain.usecase.search

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.domain.repository.MarketRepository
import javax.inject.Inject

class SearchSymbolsUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    suspend operator fun invoke(query: String): AppResult<List<SearchResult>> =
        repository.search(KoreanStockAliases.resolve(query) ?: query)
}
