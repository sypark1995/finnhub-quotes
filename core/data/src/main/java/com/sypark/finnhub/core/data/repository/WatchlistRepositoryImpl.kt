package com.sypark.finnhub.core.data.repository

import com.sypark.finnhub.core.common.AppDispatchers
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.UiError
import com.sypark.finnhub.core.data.mapper.toDomain
import com.sypark.finnhub.core.data.mapper.toEntity
import com.sypark.finnhub.core.database.dao.WatchlistDao
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WatchlistRepositoryImpl @Inject constructor(
    private val dao: WatchlistDao,
    private val dispatchers: AppDispatchers,
) : WatchlistRepository {

    override fun observeWatchlist(): Flow<List<WatchlistItem>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun add(item: WatchlistItem): AppResult<Unit> = withContext(dispatchers.io) {
        runCatching { dao.insert(item.toEntity(addedAt = System.currentTimeMillis())) }
            .fold({ AppResult.Success(Unit) }, { AppResult.Error(UiError.Unknown(it.message ?: "insert failed")) })
    }

    override suspend fun remove(symbol: String): AppResult<Unit> = withContext(dispatchers.io) {
        runCatching { dao.delete(symbol) }
            .fold({ AppResult.Success(Unit) }, { AppResult.Error(UiError.Unknown(it.message ?: "delete failed")) })
    }

    override suspend fun reorder(items: List<WatchlistItem>): AppResult<Unit> = withContext(dispatchers.io) {
        runCatching {
            items.forEachIndexed { index, item -> dao.updateSortOrder(item.symbol, index) }
        }.fold({ AppResult.Success(Unit) }, { AppResult.Error(UiError.Unknown(it.message ?: "reorder failed")) })
    }

    override suspend fun isInWatchlist(symbol: String): Boolean = withContext(dispatchers.io) {
        dao.getBySymbol(symbol) != null
    }
}
