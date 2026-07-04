package com.sypark.finnhub.core.domain.usecase.watchlist

import app.cash.turbine.test
import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ObserveConnectionStatusUseCaseTest {

    @Test
    fun `invoke delegates to repository observeConnectionStatus`() = runTest {
        val repository = mockk<MarketRepository>()
        every { repository.observeConnectionStatus() } returns flowOf(ConnectionStatus.Connected)

        ObserveConnectionStatusUseCase(repository)().test {
            assertEquals(ConnectionStatus.Connected, awaitItem())
            awaitComplete()
        }
    }
}
