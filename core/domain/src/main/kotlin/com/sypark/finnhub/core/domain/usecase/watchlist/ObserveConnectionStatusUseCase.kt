package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.domain.repository.MarketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectionStatusUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    operator fun invoke(): Flow<ConnectionStatus> = repository.observeConnectionStatus()
}
