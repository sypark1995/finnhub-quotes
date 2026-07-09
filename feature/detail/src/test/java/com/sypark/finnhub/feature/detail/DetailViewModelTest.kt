package com.sypark.finnhub.feature.detail

import androidx.lifecycle.SavedStateHandle
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.domain.model.StockMetrics
import com.sypark.finnhub.core.domain.model.StockProfile
import com.sypark.finnhub.core.domain.usecase.detail.GetCandlesUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetCompanyNewsUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetPeersUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetQuoteUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetStockMetricsUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetStockProfileUseCase
import com.sypark.finnhub.core.domain.usecase.detail.IsInWatchlistUseCase
import com.sypark.finnhub.core.domain.usecase.detail.ToggleWatchlistUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getQuoteUseCase = mockk<GetQuoteUseCase>()
    private val getStockProfileUseCase = mockk<GetStockProfileUseCase>()
    private val getStockMetricsUseCase = mockk<GetStockMetricsUseCase>()
    private val getPeersUseCase = mockk<GetPeersUseCase>()
    private val getCandlesUseCase = mockk<GetCandlesUseCase>()
    private val getCompanyNewsUseCase = mockk<GetCompanyNewsUseCase>()
    private val toggleWatchlistUseCase = mockk<ToggleWatchlistUseCase>()
    private val isInWatchlistUseCase = mockk<IsInWatchlistUseCase>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        coEvery { getQuoteUseCase("AAPL") } returns AppResult.Success(
            Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.REST),
        )
        coEvery { getStockProfileUseCase("AAPL") } returns AppResult.Success(
            StockProfile("AAPL", "Apple Inc.", "NASDAQ", "Technology", "https://x", 3_010_000_000_000.0, "https://apple.com", "USD"),
        )
        coEvery { getStockMetricsUseCase("AAPL") } returns AppResult.Success(StockMetrics("AAPL", 32.5, 199.62, 164.08, 6.1, 1.2))
        coEvery { getPeersUseCase("AAPL") } returns AppResult.Success(listOf("MSFT"))
        coEvery { getCandlesUseCase("AAPL", "D", any(), any()) } returns AppResult.Success(emptyList())
        coEvery { getCompanyNewsUseCase("AAPL", any(), any()) } returns AppResult.Success(emptyList())
        coEvery { isInWatchlistUseCase("AAPL") } returns false
    }

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel() = DetailViewModel(
        SavedStateHandle(mapOf("symbol" to "AAPL")),
        getQuoteUseCase, getStockProfileUseCase, getStockMetricsUseCase,
        getPeersUseCase, getCandlesUseCase, getCompanyNewsUseCase, toggleWatchlistUseCase, isInWatchlistUseCase,
    )

    @Test
    fun `Load fetches quote, profile, metrics, peers, and candles for the nav-arg symbol`() = runTest(dispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(DetailIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("AAPL", state.symbol)
        assertEquals("$198.50", state.quote?.price)
        assertEquals("Apple Inc.", state.profile?.name)
        assertEquals(listOf("MSFT"), state.peers)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `Load initializes isInWatchlist from the repository's actual membership`() = runTest(dispatcher) {
        coEvery { isInWatchlistUseCase("AAPL") } returns true
        val viewModel = buildViewModel()

        viewModel.onIntent(DetailIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.state.value.isInWatchlist)
    }

    @Test
    fun `SelectTab updates selectedTab without refetching`() = runTest(dispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(DetailIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DetailIntent.SelectTab(DetailTab.PROFILE))

        assertEquals(DetailTab.PROFILE, viewModel.state.value.selectedTab)
    }

    @Test
    fun `ChangeResolution refetches candles at the new resolution`() = runTest(dispatcher) {
        coEvery { getCandlesUseCase("AAPL", "W", any(), any()) } returns AppResult.Success(emptyList())
        val viewModel = buildViewModel()
        viewModel.onIntent(DetailIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DetailIntent.ChangeResolution(ChartResolution.WEEK))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(ChartResolution.WEEK, viewModel.state.value.chartResolution)
        io.mockk.coVerify { getCandlesUseCase("AAPL", "W", any(), any()) }
    }
}
