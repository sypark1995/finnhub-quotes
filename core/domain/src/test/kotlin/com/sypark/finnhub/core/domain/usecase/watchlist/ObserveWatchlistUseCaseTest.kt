package com.sypark.finnhub.core.domain.usecase.watchlist

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

class ObserveWatchlistUseCaseTest {

    @Test
    fun `invoke delegates straight to the repository`() = runTest {
        val repository = mockk<WatchlistRepository>()
        val items = listOf(WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0))
        every { repository.observeWatchlist() } returns flowOf(items)

        val useCase = ObserveWatchlistUseCase(repository)

        useCase().test {
            assertEquals(items, awaitItem())
            awaitComplete()
        }
    }
}
