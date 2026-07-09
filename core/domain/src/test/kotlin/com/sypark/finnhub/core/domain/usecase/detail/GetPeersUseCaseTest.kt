package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetPeersUseCaseTest {
    @Test
    fun `invoke delegates to repository getPeers`() = runTest {
        val repository = mockk<MarketRepository>()
        coEvery { repository.getPeers("AAPL") } returns AppResult.Success(listOf("MSFT", "GOOGL"))

        assertEquals(AppResult.Success(listOf("MSFT", "GOOGL")), GetPeersUseCase(repository)("AAPL"))
    }
}
