package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.Candle
import com.sypark.finnhub.core.domain.repository.MarketRepository
import javax.inject.Inject

class GetCandlesUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    suspend operator fun invoke(symbol: String, resolution: String, from: Long, to: Long): AppResult<List<Candle>> =
        repository.getCandles(symbol, resolution, from, to)
}
