package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.StockMetrics
import com.sypark.finnhub.core.domain.repository.MarketRepository
import javax.inject.Inject

class GetStockMetricsUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    suspend operator fun invoke(symbol: String): AppResult<StockMetrics> = repository.getStockMetrics(symbol)
}
