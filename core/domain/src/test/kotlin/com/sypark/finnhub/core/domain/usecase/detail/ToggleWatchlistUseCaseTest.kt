package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ToggleWatchlistUseCaseTest {

    private val item = WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, sortOrder = 0)

    @Test
    fun `invoke adds the item when it is not already in the watchlist`() = runTest {
        val repository = mockk<WatchlistRepository>()
        coEvery { repository.isInWatchlist("AAPL") } returns false
        coEvery { repository.add(item) } returns AppResult.Success(Unit)

        assertEquals(AppResult.Success(Unit), ToggleWatchlistUseCase(repository)(item))
    }

    @Test
    fun `invoke removes the item when it is already in the watchlist`() = runTest {
        val repository = mockk<WatchlistRepository>()
        coEvery { repository.isInWatchlist("AAPL") } returns true
        coEvery { repository.remove("AAPL") } returns AppResult.Success(Unit)

        assertEquals(AppResult.Success(Unit), ToggleWatchlistUseCase(repository)(item))
    }
}
