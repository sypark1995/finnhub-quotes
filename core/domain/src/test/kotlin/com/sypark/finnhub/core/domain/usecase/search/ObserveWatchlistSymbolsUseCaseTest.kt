package com.sypark.finnhub.core.domain.usecase.search

import app.cash.turbine.test
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ObserveWatchlistSymbolsUseCaseTest {
    @Test
    fun `invoke reduces the watchlist to a set of symbols`() = runTest {
        val repository = mockk<WatchlistRepository>()
        every { repository.observeWatchlist() } returns flowOf(listOf(WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0)))

        ObserveWatchlistSymbolsUseCase(repository)().test {
            assertEquals(setOf("AAPL"), awaitItem())
            awaitComplete()
        }
    }
}
