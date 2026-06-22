package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import javax.inject.Inject

class ReorderWatchlistUseCase @Inject constructor(
    private val repository: WatchlistRepository,
) {
    suspend operator fun invoke(fromIndex: Int, toIndex: Int, currentItems: List<WatchlistItem>): AppResult<Unit> {
        val mutable = currentItems.toMutableList()
        val moved = mutable.removeAt(fromIndex)
        mutable.add(toIndex, moved)
        val reindexed = mutable.mapIndexed { index, item -> item.copy(sortOrder = index) }
        return repository.reorder(reindexed)
    }
}
