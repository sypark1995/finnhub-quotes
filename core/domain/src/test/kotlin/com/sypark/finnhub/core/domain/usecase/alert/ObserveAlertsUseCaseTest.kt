package com.sypark.finnhub.core.domain.usecase.alert

import app.cash.turbine.test
import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.repository.AlertRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ObserveAlertsUseCaseTest {
    @Test
    fun `invoke delegates to repository observeAlerts`() = runTest {
        val repository = mockk<AlertRepository>()
        val alerts = listOf(PriceAlert(1, "AAPL", 210.0, AlertCondition.ABOVE, true, null))
        every { repository.observeAlerts() } returns flowOf(alerts)

        ObserveAlertsUseCase(repository)().test {
            assertEquals(alerts, awaitItem())
            awaitComplete()
        }
    }
}
