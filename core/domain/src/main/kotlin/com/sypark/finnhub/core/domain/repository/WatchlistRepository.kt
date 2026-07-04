package com.sypark.finnhub.core.domain.repository

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun observeWatchlist(): Flow<List<WatchlistItem>>
    suspend fun add(item: WatchlistItem): AppResult<Unit>
    suspend fun remove(symbol: String): AppResult<Unit>
    suspend fun reorder(items: List<WatchlistItem>): AppResult<Unit>
    suspend fun isInWatchlist(symbol: String): Boolean
}
