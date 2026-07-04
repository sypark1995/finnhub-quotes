package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RemoveFromWatchlistUseCaseTest {

    @Test
    fun `invoke delegates to repository remove`() = runTest {
        val repository = mockk<WatchlistRepository>()
        coEvery { repository.remove("AAPL") } returns AppResult.Success(Unit)

        val result = RemoveFromWatchlistUseCase(repository)("AAPL")

        assertEquals(AppResult.Success(Unit), result)
    }
}
