package com.sypark.finnhub.core.domain.usecase.search

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class AddToWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(result: SearchResult, sortOrder: Int): AppResult<Unit> = repository.add(
        WatchlistItem(
            symbol = result.symbol,
            displayName = result.description,
            assetType = result.assetType,
            sortOrder = sortOrder,
        ),
    )
}
