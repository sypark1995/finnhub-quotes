package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IsInWatchlistUseCaseTest {
    @Test
    fun `invoke delegates to repository isInWatchlist`() = runTest {
        val repository = mockk<WatchlistRepository>()
        coEvery { repository.isInWatchlist("AAPL") } returns true

        assertEquals(true, IsInWatchlistUseCase(repository)("AAPL"))
    }
}
