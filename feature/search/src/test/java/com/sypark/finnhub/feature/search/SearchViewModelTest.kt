package com.sypark.finnhub.feature.search

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.domain.usecase.search.AddToWatchlistUseCase
import com.sypark.finnhub.core.domain.usecase.search.ObserveWatchlistSymbolsUseCase
import com.sypark.finnhub.core.domain.usecase.search.SearchSymbolsUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.RemoveFromWatchlistUseCase
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
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val searchSymbolsUseCase = mockk<SearchSymbolsUseCase>()
    private val addToWatchlistUseCase = mockk<AddToWatchlistUseCase>()
    private val observeWatchlistSymbolsUseCase = mockk<ObserveWatchlistSymbolsUseCase>()
    private val removeFromWatchlistUseCase = mockk<RemoveFromWatchlistUseCase>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { observeWatchlistSymbolsUseCase() } returns flowOf(emptySet())
    }

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel() = SearchViewModel(
        searchSymbolsUseCase, addToWatchlistUseCase, observeWatchlistSymbolsUseCase, removeFromWatchlistUseCase,
    )

    @Test
    fun `QueryChanged waits 300ms of silence before calling the use case`() = runTest(dispatcher) {
        coEvery { searchSymbolsUseCase("AAPL") } returns AppResult.Success(
            listOf(SearchResult("AAPL", "Apple Inc.", AssetType.STOCK)),
        )
        val viewModel = buildViewModel()

        viewModel.onIntent(SearchIntent.QueryChanged("A"))
        dispatcher.scheduler.advanceTimeBy(100)
        viewModel.onIntent(SearchIntent.QueryChanged("AA"))
        dispatcher.scheduler.advanceTimeBy(100)
        viewModel.onIntent(SearchIntent.QueryChanged("AAPL"))
        dispatcher.scheduler.advanceTimeBy(299)

        assertEquals(true, viewModel.state.value.results.isEmpty())

        dispatcher.scheduler.advanceTimeBy(2)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf("AAPL"), viewModel.state.value.results.map { it.symbol })
    }

    @Test
    fun `FilterChanged filters results without re-querying`() = runTest(dispatcher) {
        coEvery { searchSymbolsUseCase("AAPL") } returns AppResult.Success(
            listOf(
                SearchResult("AAPL", "Apple Inc.", AssetType.STOCK),
                SearchResult("BINANCE:BTCUSDT", "Bitcoin", AssetType.CRYPTO),
            ),
        )
        val viewModel = buildViewModel()
        viewModel.onIntent(SearchIntent.QueryChanged("AAPL"))
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(SearchIntent.FilterChanged(AssetTypeFilter.STOCK))

        assertEquals(listOf("AAPL"), viewModel.state.value.results.map { it.symbol })
    }
}
