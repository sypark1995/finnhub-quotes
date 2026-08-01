package com.sypark.finnhub.feature.earnings

import app.cash.turbine.test
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.common.UiError
import com.sypark.finnhub.core.domain.model.EarningsEvent
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.usecase.earnings.GetEarningsCalendarUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveWatchlistUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EarningsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val observeWatchlistUseCase = mockk<ObserveWatchlistUseCase>()
    private val getEarningsCalendarUseCase = mockk<GetEarningsCalendarUseCase>()

    @BeforeEach
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel() = EarningsViewModel(observeWatchlistUseCase, getEarningsCalendarUseCase)

    @Test
    fun `Load with an empty watchlist yields an empty, non-loading state without calling the calendar use case`() = runTest(dispatcher) {
        every { observeWatchlistUseCase() } returns flowOf(emptyList())
        val viewModel = buildViewModel()

        viewModel.onIntent(EarningsIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(emptyList<EarningsEventUi>(), viewModel.state.value.events)
        assertEquals(false, viewModel.state.value.isLoading)
        io.mockk.coVerify(exactly = 0) { getEarningsCalendarUseCase(any(), any(), any()) }
    }

    @Test
    fun `Load fetches every watchlist symbol's calendar in parallel and merges the results sorted by date`() = runTest(dispatcher) {
        every { observeWatchlistUseCase() } returns flowOf(
            listOf(
                WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0),
                WatchlistItem("MSFT", "Microsoft", AssetType.STOCK, 1),
            ),
        )
        coEvery { getEarningsCalendarUseCase(any(), any(), "AAPL") } returns AppResult.Success(
            listOf(EarningsEvent("AAPL", "2026-10-28", "amc", 2.05, null, null, null)),
        )
        coEvery { getEarningsCalendarUseCase(any(), any(), "MSFT") } returns AppResult.Success(
            listOf(EarningsEvent("MSFT", "2026-07-22", "amc", 3.1, null, null, null)),
        )
        val viewModel = buildViewModel()

        viewModel.onIntent(EarningsIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        val events = viewModel.state.value.events
        assertEquals(listOf("MSFT", "AAPL"), events.map { it.symbol })
        assertEquals("Microsoft", events.first().displayName)
        assertEquals("2026.07.22", events.first().dateText)
        assertEquals("장 마감 후", events.first().timingText)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `Load does not blank the whole list when one symbol's calendar call fails`() = runTest(dispatcher) {
        every { observeWatchlistUseCase() } returns flowOf(
            listOf(
                WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0),
                WatchlistItem("MSFT", "Microsoft", AssetType.STOCK, 1),
            ),
        )
        coEvery { getEarningsCalendarUseCase(any(), any(), "AAPL") } returns AppResult.Error(UiError.Network)
        coEvery { getEarningsCalendarUseCase(any(), any(), "MSFT") } returns AppResult.Success(
            listOf(EarningsEvent("MSFT", "2026-07-22", "bmo", 3.1, null, null, null)),
        )
        val viewModel = buildViewModel()

        viewModel.onIntent(EarningsIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("MSFT"), viewModel.state.value.events.map { it.symbol })
    }

    @Test
    fun `an EarningsEvent with no epsEstimate renders a placeholder text`() = runTest(dispatcher) {
        every { observeWatchlistUseCase() } returns flowOf(listOf(WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0)))
        coEvery { getEarningsCalendarUseCase(any(), any(), "AAPL") } returns AppResult.Success(
            listOf(EarningsEvent("AAPL", "2026-10-28", "", null, null, null, null)),
        )
        val viewModel = buildViewModel()

        viewModel.onIntent(EarningsIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        val event = viewModel.state.value.events.single()
        assertEquals("EPS 예상치 없음", event.epsEstimateText)
        assertEquals("", event.timingText)
    }

    @Test
    fun `OpenDetail emits a NavigateToDetail effect with the given symbol`() = runTest(dispatcher) {
        val viewModel = buildViewModel()
        viewModel.effect.test {
            viewModel.onIntent(EarningsIntent.OpenDetail("AAPL"))
            assertEquals(EarningsEffect.NavigateToDetail("AAPL"), awaitItem())
        }
    }
}
