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

class CreateAlertUseCaseTest {
    @Test
    fun `invoke builds a new, unenabled-toggle-defaulted-to-true, untriggered PriceAlert`() = runTest {
        val repository = mockk<AlertRepository>()
        coEvery { repository.create(PriceAlert(0, "AAPL", 210.0, AlertCondition.ABOVE, true, null)) } returns AppResult.Success(1L)

        val result = CreateAlertUseCase(repository)("AAPL", 210.0, AlertCondition.ABOVE)

        assertEquals(AppResult.Success(1L), result)
    }
}
