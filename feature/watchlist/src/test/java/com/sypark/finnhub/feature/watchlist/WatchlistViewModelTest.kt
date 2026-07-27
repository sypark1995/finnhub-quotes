package com.sypark.finnhub.feature.watchlist

import app.cash.turbine.test
import com.sypark.finnhub.core.common.AppCoroutineScope
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.usecase.watchlist.DisconnectMarketUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveConnectionStatusUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveQuotesUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveWatchlistUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.RefreshQuotesUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ReorderWatchlistUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class WatchlistViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val observeWatchlistUseCase = mockk<ObserveWatchlistUseCase>()
    private val observeQuotesUseCase = mockk<ObserveQuotesUseCase>()
    private val observeConnectionStatusUseCase = mockk<ObserveConnectionStatusUseCase>()
    private val removeFromWatchlistUseCase = mockk<RemoveFromWatchlistUseCase>()
    private val reorderWatchlistUseCase = mockk<ReorderWatchlistUseCase>()
    private val refreshQuotesUseCase = mockk<RefreshQuotesUseCase>()
    private val disconnectMarketUseCase = mockk<DisconnectMarketUseCase>()
    private val appCoroutineScope = mockk<AppCoroutineScope>(relaxed = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { observeWatchlistUseCase() } returns flowOf(
            listOf(WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0)),
        )
        every { observeQuotesUseCase(setOf("AAPL")) } returns flowOf(
            mapOf("AAPL" to Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.WEBSOCKET)),
        )
        every { observeConnectionStatusUseCase() } returns flowOf(ConnectionStatus.Connected)
    }

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel() = WatchlistViewModel(
        observeWatchlistUseCase, observeQuotesUseCase, observeConnectionStatusUseCase,
        removeFromWatchlistUseCase, reorderWatchlistUseCase, refreshQuotesUseCase,
        disconnectMarketUseCase, appCoroutineScope,
    )

    @Test
    fun `Load populates items, quotes, and connectionStatus from the three flows`() = runTest(dispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(WatchlistIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf("AAPL"), state.items.map { it.symbol })
        assertEquals("$198.50", state.quotes.getValue("AAPL").price)
        assertEquals(ConnectionStatus.Connected, state.connectionStatus)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `Remove calls the use case and shows a snackbar on failure`() = runTest(dispatcher) {
        coEvery { removeFromWatchlistUseCase("AAPL") } returns AppResult.Error(com.sypark.finnhub.core.common.UiError.Network)
        val viewModel = buildViewModel()
        viewModel.onIntent(WatchlistIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(WatchlistIntent.Remove("AAPL"))
            dispatcher.scheduler.advanceUntilIdle()
            val effect = awaitItem()
            assert(effect is WatchlistEffect.ShowSnackbar)
        }
    }

    @Test
    fun `OpenDetail emits a NavigateToDetail effect`() = runTest(dispatcher) {
        val viewModel = buildViewModel()
        viewModel.effect.test {
            viewModel.onIntent(WatchlistIntent.OpenDetail("AAPL", AssetType.STOCK))
            assertEquals(WatchlistEffect.NavigateToDetail("AAPL", AssetType.STOCK), awaitItem())
        }
    }
}
