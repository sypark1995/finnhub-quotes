package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetQuoteUseCaseTest {
    @Test
    fun `invoke delegates to repository getQuote`() = runTest {
        val repository = mockk<MarketRepository>()
        val quote = Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.REST)
        coEvery { repository.getQuote("AAPL") } returns AppResult.Success(quote)

        assertEquals(AppResult.Success(quote), GetQuoteUseCase(repository)("AAPL"))
    }
}
