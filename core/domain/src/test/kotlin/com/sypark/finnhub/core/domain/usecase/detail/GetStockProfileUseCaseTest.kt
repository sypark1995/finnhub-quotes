package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.StockProfile
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetStockProfileUseCaseTest {
    @Test
    fun `invoke delegates to repository getStockProfile`() = runTest {
        val repository = mockk<MarketRepository>()
        val profile = StockProfile("AAPL", "Apple Inc.", "NASDAQ", "Technology", "https://x", 3_010_000.0, "https://apple.com", "USD")
        coEvery { repository.getStockProfile("AAPL") } returns AppResult.Success(profile)

        assertEquals(AppResult.Success(profile), GetStockProfileUseCase(repository)("AAPL"))
    }
}
