package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.News
import com.sypark.finnhub.core.domain.repository.MarketRepository
import javax.inject.Inject

class GetCompanyNewsUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    suspend operator fun invoke(symbol: String, from: String, to: String): AppResult<List<News>> =
        repository.getCompanyNews(symbol, from, to)
}
