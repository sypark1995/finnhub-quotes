package com.sypark.finnhub.core.domain.usecase.detail

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.News
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetCompanyNewsUseCaseTest {
    @Test
    fun `invoke delegates to repository getCompanyNews`() = runTest {
        val repository = mockk<MarketRepository>()
        val news = listOf(News(1L, "Headline", "Reuters", "https://x", 1L, "...", "https://x/i.png"))
        coEvery { repository.getCompanyNews("AAPL", "2026-06-01", "2026-07-01") } returns AppResult.Success(news)

        assertEquals(AppResult.Success(news), GetCompanyNewsUseCase(repository)("AAPL", "2026-06-01", "2026-07-01"))
    }
}
