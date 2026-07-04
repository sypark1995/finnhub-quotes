package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReorderWatchlistUseCaseTest {

    @Test
    fun `invoke moves the item from fromIndex to toIndex and re-derives every sortOrder`() = runTest {
        val repository = mockk<WatchlistRepository>()
        val captured = slot<List<WatchlistItem>>()
        coEvery { repository.reorder(capture(captured)) } returns AppResult.Success(Unit)
        val items = listOf(
            WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0),
            WatchlistItem("MSFT", "Microsoft", AssetType.STOCK, 1),
            WatchlistItem("TSLA", "Tesla", AssetType.STOCK, 2),
        )

        ReorderWatchlistUseCase(repository)(fromIndex = 0, toIndex = 2, currentItems = items)

        val result = captured.captured
        assertEquals(listOf("MSFT", "TSLA", "AAPL"), result.map { it.symbol })
        assertEquals(listOf(0, 1, 2), result.map { it.sortOrder })
    }
}
