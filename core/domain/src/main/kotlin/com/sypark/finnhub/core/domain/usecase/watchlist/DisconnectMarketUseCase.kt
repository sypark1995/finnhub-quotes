package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.domain.repository.MarketRepository
import javax.inject.Inject

class DisconnectMarketUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    suspend operator fun invoke() = repository.disconnect()
}
