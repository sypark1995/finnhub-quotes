package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository,
) {
    operator fun invoke(): Flow<List<WatchlistItem>> = repository.observeWatchlist()
}
