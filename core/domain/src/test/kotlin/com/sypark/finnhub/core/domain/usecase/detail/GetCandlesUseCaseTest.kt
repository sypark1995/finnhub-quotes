package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.Candle
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetCandlesUseCaseTest {
    @Test
    fun `invoke delegates to repository getCandles`() = runTest {
        val repository = mockk<MarketRepository>()
        val candles = listOf(Candle(1L, 197.2, 199.1, 196.8, 198.5, 1000L))
        coEvery { repository.getCandles("AAPL", "D", 1L, 2L) } returns AppResult.Success(candles)

        assertEquals(AppResult.Success(candles), GetCandlesUseCase(repository)("AAPL", "D", 1L, 2L))
    }
}
