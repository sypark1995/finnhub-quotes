package com.sypark.finnhub.core.domain.usecase.search

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.domain.repository.MarketRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchSymbolsUseCaseTest {
    @Test
    fun `invoke delegates to repository search`() = runTest {
        val repository = mockk<MarketRepository>()
        val results = listOf(SearchResult("AAPL", "Apple Inc.", AssetType.STOCK))
        coEvery { repository.search("AAPL") } returns AppResult.Success(results)

        assertEquals(AppResult.Success(results), SearchSymbolsUseCase(repository)("AAPL"))
    }
}
