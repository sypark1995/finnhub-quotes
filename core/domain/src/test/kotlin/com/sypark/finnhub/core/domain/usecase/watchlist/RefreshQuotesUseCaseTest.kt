package com.sypark.finnhub.core.domain.usecase.watchlist

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RefreshQuotesUseCaseTest {

    @Test
    fun `invoke fetches every symbol and ignores individual failures`() = runTest {
        val repository = mockk<MarketRepository>()
        coEvery { repository.getQuote("AAPL") } returns AppResult.Success(
            Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.REST),
        )
        coEvery { repository.getQuote("MSFT") } returns AppResult.Error(com.sypark.finnhub.core.common.UiError.Network)

        RefreshQuotesUseCase(repository)(setOf("AAPL", "MSFT"))

        coVerify { repository.getQuote("AAPL") }
        coVerify { repository.getQuote("MSFT") }
    }
}
