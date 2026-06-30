package com.sypark.finnhub.core.domain.usecase.search

import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveWatchlistSymbolsUseCase @Inject constructor(
    private val repository: WatchlistRepository,
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeWatchlist().map { items -> items.map { it.symbol }.toSet() }
}
