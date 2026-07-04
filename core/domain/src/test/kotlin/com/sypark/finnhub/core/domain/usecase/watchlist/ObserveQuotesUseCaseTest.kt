package com.sypark.finnhub.core.domain.usecase.watchlist

import app.cash.turbine.test
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ObserveQuotesUseCaseTest {

    @Test
    fun `invoke passes the requested symbol set through to the repository`() = runTest {
        val repository = mockk<MarketRepository>()
        val quote = Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.WEBSOCKET)
        every { repository.observeQuotes(setOf("AAPL")) } returns flowOf(mapOf("AAPL" to quote))

        val useCase = ObserveQuotesUseCase(repository)

        useCase(setOf("AAPL")).test {
            assertEquals(mapOf("AAPL" to quote), awaitItem())
            awaitComplete()
        }
    }
}
