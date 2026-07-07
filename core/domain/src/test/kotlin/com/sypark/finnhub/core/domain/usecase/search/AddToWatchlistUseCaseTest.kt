package com.sypark.finnhub.core.domain.usecase.search

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AddToWatchlistUseCaseTest {
    @Test
    fun `invoke builds a WatchlistItem from the SearchResult and the given sortOrder`() = runTest {
        val repository = mockk<WatchlistRepository>()
        coEvery { repository.add(WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 3)) } returns AppResult.Success(Unit)

        val result = AddToWatchlistUseCase(repository)(SearchResult("AAPL", "Apple Inc.", AssetType.STOCK), sortOrder = 3)

        assertEquals(AppResult.Success(Unit), result)
    }
}
