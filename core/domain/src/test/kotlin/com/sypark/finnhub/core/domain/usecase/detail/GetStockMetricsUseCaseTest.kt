package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.StockMetrics
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetStockMetricsUseCaseTest {
    @Test
    fun `invoke delegates to repository getStockMetrics`() = runTest {
        val repository = mockk<MarketRepository>()
        val metrics = StockMetrics("AAPL", 32.5, 199.62, 164.08, 6.1, 1.2)
        coEvery { repository.getStockMetrics("AAPL") } returns AppResult.Success(metrics)

        assertEquals(AppResult.Success(metrics), GetStockMetricsUseCase(repository)("AAPL"))
    }
}
