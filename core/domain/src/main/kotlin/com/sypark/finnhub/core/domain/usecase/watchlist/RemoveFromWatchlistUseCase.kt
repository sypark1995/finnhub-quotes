package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class RemoveFromWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(symbol: String): AppResult<Unit> = repository.remove(symbol)
}
