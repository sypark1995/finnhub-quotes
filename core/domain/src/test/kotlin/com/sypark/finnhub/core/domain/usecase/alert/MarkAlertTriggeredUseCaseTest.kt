package com.sypark.finnhub.core.domain.usecase.alert

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.repository.AlertRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MarkAlertTriggeredUseCaseTest {
    @Test
    fun `invoke delegates to repository markTriggered`() = runTest {
        val repository = mockk<AlertRepository>()
        coEvery { repository.markTriggered(1L) } returns AppResult.Success(Unit)
        assertEquals(AppResult.Success(Unit), MarkAlertTriggeredUseCase(repository)(1L))
    }
}
