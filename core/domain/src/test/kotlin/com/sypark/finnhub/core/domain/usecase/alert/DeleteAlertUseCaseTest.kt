package com.sypark.finnhub.core.domain.usecase.alert

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.repository.AlertRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeleteAlertUseCaseTest {
    @Test
    fun `invoke delegates to repository delete`() = runTest {
        val repository = mockk<AlertRepository>()
        coEvery { repository.delete(1L) } returns AppResult.Success(Unit)
        assertEquals(AppResult.Success(Unit), DeleteAlertUseCase(repository)(1L))
    }
}
