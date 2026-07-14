package com.sypark.finnhub.core.domain.usecase.alert

import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.repository.AlertRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateAlertUseCaseTest {
    @Test
    fun `invoke delegates to repository update`() = runTest {
        val repository = mockk<AlertRepository>()
        val alert = PriceAlert(1, "AAPL", 210.0, AlertCondition.ABOVE, false, null)
        coEvery { repository.update(alert) } returns AppResult.Success(Unit)

        assertEquals(AppResult.Success(Unit), UpdateAlertUseCase(repository)(alert))
    }
}
