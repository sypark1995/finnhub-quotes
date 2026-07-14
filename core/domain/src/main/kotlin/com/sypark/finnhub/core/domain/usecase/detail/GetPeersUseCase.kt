package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.repository.MarketRepository
import javax.inject.Inject

class GetPeersUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    suspend operator fun invoke(symbol: String): AppResult<List<String>> = repository.getPeers(symbol)
}
