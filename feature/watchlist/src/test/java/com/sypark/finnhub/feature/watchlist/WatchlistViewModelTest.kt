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
import com.sypark.finnhub.core.domain.usecase.watchlist.PopularCryptoSymbols
import com.sypark.finnhub.core.domain.usecase.watchlist.PopularSymbols
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
        every { observeQuotesUseCase(setOf("AAPL") + PopularSymbols.SYMBOLS + PopularCryptoSymbols.SYMBOLS) } returns flowOf(
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
    fun `Load derives popularStocks from the curated symbols, sorted by absolute change desc`() = runTest(dispatcher) {
        every { observeQuotesUseCase(setOf("AAPL") + PopularSymbols.SYMBOLS + PopularCryptoSymbols.SYMBOLS) } returns flowOf(
            mapOf(
                "AAPL" to Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.WEBSOCKET),
                "NVDA" to Quote("NVDA", 900.0, 45.0, 5.26, 905.0, 850.0, 855.0, 855.0, 1L, QuoteSource.WEBSOCKET),
                "MSFT" to Quote("MSFT", 420.0, -8.4, -1.96, 428.0, 415.0, 428.4, 428.4, 1L, QuoteSource.WEBSOCKET),
            ),
        )
        val viewModel = buildViewModel()
        viewModel.onIntent(WatchlistIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        // AAPL is both the user's watchlist item and a curated popular symbol, so it
        // appears in popularStocks too (abs 1.17%), ranked behind NVDA (5.26%) and MSFT (1.96%).
        val popular = viewModel.state.value.popularStocks
        assertEquals(listOf("NVDA", "MSFT", "AAPL"), popular.map { it.symbol })
    }

    @Test
    fun `Load derives popularCrypto from the curated crypto symbols, sorted by absolute change desc`() = runTest(dispatcher) {
        every { observeQuotesUseCase(setOf("AAPL") + PopularSymbols.SYMBOLS + PopularCryptoSymbols.SYMBOLS) } returns flowOf(
            mapOf(
                "AAPL" to Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.WEBSOCKET),
                "BINANCE:BTCUSDT" to Quote("BINANCE:BTCUSDT", 65000.0, 800.0, 1.25, 65500.0, 64000.0, 64200.0, 64200.0, 1L, QuoteSource.WEBSOCKET),
                "BINANCE:DOGEUSDT" to Quote("BINANCE:DOGEUSDT", 0.15, -0.02, -11.76, 0.17, 0.14, 0.17, 0.17, 1L, QuoteSource.WEBSOCKET),
            ),
        )
        val viewModel = buildViewModel()
        viewModel.onIntent(WatchlistIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        val popularCrypto = viewModel.state.value.popularCrypto
        assertEquals(listOf("BINANCE:DOGEUSDT", "BINANCE:BTCUSDT"), popularCrypto.map { it.symbol })
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
