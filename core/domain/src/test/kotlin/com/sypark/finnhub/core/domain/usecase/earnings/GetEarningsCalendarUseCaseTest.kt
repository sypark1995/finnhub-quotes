package com.sypark.finnhub.core.domain.usecase.earnings

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.EarningsEvent
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetEarningsCalendarUseCaseTest {
    @Test
    fun `invoke delegates to repository getEarningsCalendar`() = runTest {
        val repository = mockk<MarketRepository>()
        val events = listOf(EarningsEvent("AAPL", "2026-07-30", "amc", 1.93, null, null, null))
        coEvery { repository.getEarningsCalendar("2026-07-17", "2027-07-17", "AAPL") } returns AppResult.Success(events)

        assertEquals(
            AppResult.Success(events),
            GetEarningsCalendarUseCase(repository)("2026-07-17", "2027-07-17", "AAPL"),
        )
    }
}
