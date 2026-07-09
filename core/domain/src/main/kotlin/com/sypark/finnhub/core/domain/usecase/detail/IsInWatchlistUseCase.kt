package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class IsInWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(symbol: String): Boolean = repository.isInWatchlist(symbol)
}
