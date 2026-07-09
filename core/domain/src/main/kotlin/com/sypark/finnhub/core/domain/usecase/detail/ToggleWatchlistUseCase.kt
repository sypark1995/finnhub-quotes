package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class ToggleWatchlistUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
) {
    suspend operator fun invoke(item: WatchlistItem): AppResult<Unit> =
        if (watchlistRepository.isInWatchlist(item.symbol)) {
            watchlistRepository.remove(item.symbol)
        } else {
            watchlistRepository.add(item)
        }
}
