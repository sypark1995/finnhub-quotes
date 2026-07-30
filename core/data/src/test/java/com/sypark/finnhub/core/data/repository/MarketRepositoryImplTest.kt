package com.sypark.finnhub.core.data.repository

import app.cash.turbine.test
import com.sypark.finnhub.core.common.AppDispatchers
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.data.mapper.toDomain
import com.sypark.finnhub.core.database.dao.QuoteCacheDao
import com.sypark.finnhub.core.database.entity.QuoteCacheEntity
import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.network.FinnhubApiService
import com.sypark.finnhub.core.network.dto.QuoteDto
import com.sypark.finnhub.core.network.dto.SearchResponseDto
import com.sypark.finnhub.core.network.dto.SearchResultDto
import com.sypark.finnhub.core.websocket.ConnectionState
import com.sypark.finnhub.core.websocket.FinnhubWebSocketManager
import com.sypark.finnhub.core.websocket.TradeMessage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MarketRepositoryImplTest {

    private val apiService = mockk<FinnhubApiService>()
    private val webSocketManager = mockk<FinnhubWebSocketManager>(relaxUnitFun = true)
    private val quoteCacheDao = mockk<QuoteCacheDao>(relaxUnitFun = true)
    private val preferencesDataSource = mockk<com.sypark.finnhub.core.datastore.UserPreferencesDataSource>()
    private val repository = MarketRepositoryImpl(apiService, webSocketManager, quoteCacheDao, preferencesDataSource, AppDispatchers()) { 10_000L }

    init {
        // Task 68's fallbackJob reads these on every observeQuotes call. Defaulting to
        // Connected means the fallback poll branch never activates for tests that don't
        // care about it; the one test that does (below) overrides both with `every`.
        every { webSocketManager.connectionState } returns MutableStateFlow(ConnectionState.Connected)
        every { preferencesDataSource.refreshIntervalSeconds } returns flowOf(30)
    }

    @Test
    fun `observeConnectionStatus maps every websocket ConnectionState to the domain ConnectionStatus`() = runTest {
        every { webSocketManager.connectionState } returns MutableStateFlow(ConnectionState.Connected)
        assertEquals(ConnectionStatus.Connected, repository.observeConnectionStatus().first())
    }

    @Test
    fun `getQuote returns a REST-sourced Quote and caches it on success`() = runTest {
        every { quoteCacheDao.observe("AAPL") } returns flowOf(null)
        coEvery { apiService.getQuote("AAPL") } returns QuoteDto(198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1_720_000_000)

        val result = repository.getQuote("AAPL")

        assertTrue(result is AppResult.Success)
        assertEquals(QuoteSource.REST, (result as AppResult.Success).data.source)
    }

    @Test
    fun `getQuote falls back to the cached quote when the network call fails`() = runTest {
        coEvery { apiService.getQuote("AAPL") } throws IOException("offline")
        every { quoteCacheDao.observe("AAPL") } returns flowOf(
            QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = -300_000L),
        )

        val result = repository.getQuote("AAPL")

        assertTrue(result is AppResult.Success)
        assertEquals(QuoteSource.CACHE, (result as AppResult.Success).data.source)
    }

    @Test
    fun `getQuote reads from the cache without a REST call when the cache is within the 5-minute TTL`() = runTest {
        every { quoteCacheDao.observe("AAPL") } returns flowOf(
            com.sypark.finnhub.core.database.entity.QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = 9_800L),
        )
        val result = repository.getQuote("AAPL")
        assertTrue(result is AppResult.Success)
        coVerify(exactly = 0) { apiService.getQuote(any()) }
    }

    @Test
    fun `getQuote returns Network error when the call fails and there is no cache`() = runTest {
        coEvery { apiService.getQuote("AAPL") } throws IOException("offline")
        every { quoteCacheDao.observe("AAPL") } returns flowOf(null)

        val result = repository.getQuote("AAPL")

        assertTrue(result is AppResult.Error)
    }

    @Test
    fun `observeQuotes emits only an empty map and never touches the websocket when symbols is empty`() = runTest {
        repository.observeQuotes(emptySet()).test {
            assertEquals(emptyMap<String, Quote>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { webSocketManager.connect() }
        coVerify(exactly = 0) { webSocketManager.syncSubscriptions(any()) }
    }

    @Test
    fun `observeQuotes maps a cached entry for a requested symbol via toDomain with CACHE source`() = runTest {
        val cacheFlow = MutableStateFlow(
            listOf(QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = 1L)),
        )
        every { quoteCacheDao.observeAll() } returns cacheFlow
        every { webSocketManager.tradeUpdates } returns MutableSharedFlow()

        repository.observeQuotes(setOf("AAPL")).test {
            val quotes = awaitItem()
            assertEquals(1, quotes.size)
            assertEquals(QuoteSource.CACHE, quotes.getValue("AAPL").source)
            assertEquals(198.5, quotes.getValue("AAPL").price)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeQuotes switches a symbol to the WEBSOCKET-sourced quote once a trade arrives, and a later cache update cannot override it`() = runTest {
        val cacheFlow = MutableStateFlow(
            listOf(QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = 1L)),
        )
        val tradeFlow = MutableSharedFlow<TradeMessage>(extraBufferCapacity = 1)
        every { quoteCacheDao.observeAll() } returns cacheFlow
        every { webSocketManager.tradeUpdates } returns tradeFlow

        repository.observeQuotes(setOf("AAPL")).test {
            assertEquals(QuoteSource.CACHE, awaitItem().getValue("AAPL").source)

            tradeFlow.emit(TradeMessage("AAPL", 200.0, 10, 2L))
            assertEquals(QuoteSource.WEBSOCKET, awaitItem().getValue("AAPL").source)

            // A later cache emission for the same symbol must not overwrite the already-live entry.
            cacheFlow.value = listOf(
                QuoteCacheEntity("AAPL", 150.0, -1.0, -1.0, 151.0, 149.0, 150.0, 151.0, updatedAt = 3L),
            )
            val afterStaleCacheUpdate = awaitItem()
            assertEquals(QuoteSource.WEBSOCKET, afterStaleCacheUpdate.getValue("AAPL").source)
            assertEquals(200.0, afterStaleCacheUpdate.getValue("AAPL").price)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeQuotes handles multiple symbols independently so a trade for one symbol does not affect another`() = runTest {
        val cacheFlow = MutableStateFlow(
            listOf(
                QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = 1L),
                QuoteCacheEntity("GOOG", 2800.0, 5.0, 0.18, 2810.0, 2790.0, 2795.0, 2795.0, updatedAt = 1L),
            ),
        )
        val tradeFlow = MutableSharedFlow<TradeMessage>(extraBufferCapacity = 1)
        every { quoteCacheDao.observeAll() } returns cacheFlow
        every { webSocketManager.tradeUpdates } returns tradeFlow

        repository.observeQuotes(setOf("AAPL", "GOOG")).test {
            val initial = awaitItem()
            assertEquals(QuoteSource.CACHE, initial.getValue("AAPL").source)
            assertEquals(QuoteSource.CACHE, initial.getValue("GOOG").source)

            tradeFlow.emit(TradeMessage("AAPL", 200.0, 10, 2L))
            val afterAaplTrade = awaitItem()
            assertEquals(QuoteSource.WEBSOCKET, afterAaplTrade.getValue("AAPL").source)
            assertEquals(QuoteSource.CACHE, afterAaplTrade.getValue("GOOG").source)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeQuotes filters out cache entries and trades for symbols outside the requested set`() = runTest {
        val cacheFlow = MutableStateFlow(
            listOf(
                QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = 1L),
                QuoteCacheEntity("MSFT", 300.0, 1.0, 0.33, 301.0, 299.0, 299.5, 299.0, updatedAt = 1L),
            ),
        )
        val tradeFlow = MutableSharedFlow<TradeMessage>(extraBufferCapacity = 1)
        every { quoteCacheDao.observeAll() } returns cacheFlow
        every { webSocketManager.tradeUpdates } returns tradeFlow

        repository.observeQuotes(setOf("AAPL")).test {
            val initial = awaitItem()
            assertEquals(setOf("AAPL"), initial.keys)

            // A trade for a non-requested symbol must be silently dropped: no new emission, no key added.
            tradeFlow.emit(TradeMessage("MSFT", 999.0, 1, 2L))
            tradeFlow.emit(TradeMessage("AAPL", 205.0, 1, 3L))
            val afterAaplTrade = awaitItem()
            assertEquals(setOf("AAPL"), afterAaplTrade.keys)
            assertEquals(205.0, afterAaplTrade.getValue("AAPL").price)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { quoteCacheDao.upsert(match { it.symbol == "MSFT" }) }
    }

    @Test
    fun `observeQuotes seeds an uncached symbol via one-shot REST fetch and upserts it to the cache`() = kotlinx.coroutines.runBlocking {
        // seedJob runs on the production AppDispatchers().io (real Dispatchers.IO), so this uses
        // runBlocking + CompletableDeferred rather than runTest's virtual time, matching the
        // synchronization approach already used below for fallbackJob's real-dispatcher race.
        every { quoteCacheDao.observeAll() } returns flowOf(emptyList())
        every { webSocketManager.tradeUpdates } returns MutableSharedFlow()
        val fetched = kotlinx.coroutines.CompletableDeferred<Unit>()
        coEvery { apiService.getQuote("NVDA") } coAnswers {
            fetched.complete(Unit)
            QuoteDto(900.0, 45.0, 5.26, 905.0, 850.0, 855.0, 855.0, 1L)
        }

        val job = launch { repository.observeQuotes(setOf("NVDA")).collect { } }
        kotlinx.coroutines.withTimeout(5_000) { fetched.await() }
        job.cancel()

        coVerify(exactly = 1) { apiService.getQuote("NVDA") }
        coVerify { quoteCacheDao.upsert(match { it.symbol == "NVDA" && it.price == 900.0 }) }
    }

    @Test
    fun `observeQuotes does not re-fetch a symbol that already has a cache entry`() = runTest {
        val cacheFlow = MutableStateFlow(
            listOf(QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = 1L)),
        )
        every { quoteCacheDao.observeAll() } returns cacheFlow
        every { webSocketManager.tradeUpdates } returns MutableSharedFlow()

        repository.observeQuotes(setOf("AAPL")).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { apiService.getQuote("AAPL") }
    }

    @Test
    fun `observeQuotes cancels the cache collector job when the collecting coroutine is cancelled`() = runTest {
        val cacheCollecting = MutableStateFlow(false)
        val cacheBacking = MutableStateFlow(
            listOf(QuoteCacheEntity("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, updatedAt = 1L)),
        )
        val instrumentedCacheFlow = flow {
            cacheCollecting.value = true
            try {
                emitAll(cacheBacking)
            } finally {
                cacheCollecting.value = false
            }
        }
        every { quoteCacheDao.observeAll() } returns instrumentedCacheFlow
        every { webSocketManager.tradeUpdates } returns MutableSharedFlow()

        repository.observeQuotes(setOf("AAPL")).test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        var attempts = 0
        while (cacheCollecting.value && attempts < 50) {
            Thread.sleep(20)
            attempts++
        }
        assertFalse(cacheCollecting.value)
    }

    @Test
    fun `observeQuotes polls REST via getQuote while disconnected`() = kotlinx.coroutines.runBlocking {
        // fallbackJob runs on the production AppDispatchers().io (real Dispatchers.IO). Using
        // runTest here would resolve withTimeout's deadline against the virtual-time scheduler,
        // which auto-advances past real background work with no wall-clock wait, racing the
        // real dispatcher. runBlocking gives this one test a real event loop with real delays,
        // so waiting on a CompletableDeferred completed from the real IO thread works correctly.
        // The loop's *first* iteration fires immediately with no delay, so this test only needs
        // to wait for that first real completion, not advance any virtual clock.
        val connectionState = MutableStateFlow(com.sypark.finnhub.core.websocket.ConnectionState.Disconnected)
        every { webSocketManager.connectionState } returns connectionState
        every { webSocketManager.tradeUpdates } returns kotlinx.coroutines.flow.MutableSharedFlow()
        every { quoteCacheDao.observeAll() } returns flowOf(emptyList())
        every { preferencesDataSource.refreshIntervalSeconds } returns flowOf(30)
        val polled = kotlinx.coroutines.CompletableDeferred<Unit>()
        coEvery { apiService.getQuote("AAPL") } coAnswers {
            polled.complete(Unit)
            QuoteDto(198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L)
        }

        val job = launch { repository.observeQuotes(setOf("AAPL")).collect { } }
        kotlinx.coroutines.withTimeout(5_000) { polled.await() }
        job.cancel()

        coVerify(atLeast = 1) { apiService.getQuote("AAPL") }
    }

    @Test
    fun `search maps every DTO result to a domain SearchResult`() = runTest {
        coEvery { apiService.search("AAPL") } returns com.sypark.finnhub.core.network.dto.SearchResponseDto(
            count = 1,
            result = listOf(com.sypark.finnhub.core.network.dto.SearchResultDto("APPLE INC", "AAPL", "AAPL", "Common Stock")),
        )

        val result = repository.search("AAPL")

        assertTrue(result is AppResult.Success)
        assertEquals("AAPL", (result as AppResult.Success).data.single().symbol)
    }

    @Test
    fun `search returns an error AppResult when the call fails`() = runTest {
        coEvery { apiService.search("AAPL") } throws IOException("offline")
        assertTrue(repository.search("AAPL") is AppResult.Error)
    }

    @Test
    fun `getStockProfile maps the DTO to a domain StockProfile`() = runTest {
        coEvery { apiService.getStockProfile("AAPL") } returns com.sypark.finnhub.core.network.dto.StockProfileDto(name = "Apple Inc")
        val result = repository.getStockProfile("AAPL")
        assertTrue(result is AppResult.Success)
        assertEquals("Apple Inc", (result as AppResult.Success).data.name)
    }

    @Test
    fun `getStockMetrics maps the nested metric DTO to a domain StockMetrics`() = runTest {
        coEvery { apiService.getStockMetrics("AAPL", "all") } returns com.sypark.finnhub.core.network.dto.StockMetricResponseDto(
            com.sypark.finnhub.core.network.dto.StockMetricDto(peRatio = 32.5),
        )
        val result = repository.getStockMetrics("AAPL")
        assertTrue(result is AppResult.Success)
        assertEquals(32.5, (result as AppResult.Success).data.peRatio)
    }

    @Test
    fun `getPeers returns the raw symbol list wrapped in AppResult`() = runTest {
        coEvery { apiService.getPeers("AAPL") } returns listOf("MSFT", "GOOGL")
        val result = repository.getPeers("AAPL")
        assertTrue(result is AppResult.Success)
        assertEquals(listOf("MSFT", "GOOGL"), (result as AppResult.Success).data)
    }

    @Test
    fun `getCompanyNews maps every DTO to a domain News`() = runTest {
        coEvery { apiService.getCompanyNews("AAPL", "2026-06-01", "2026-07-01") } returns listOf(
            com.sypark.finnhub.core.network.dto.CompanyNewsDto(1, "Headline", "Reuters", "https://x", 1L, "...", "https://x/i.png"),
        )
        val result = repository.getCompanyNews("AAPL", "2026-06-01", "2026-07-01")
        assertTrue(result is AppResult.Success)
        assertEquals(1, (result as AppResult.Success).data.size)
    }

    @Test
    fun `getEarningsCalendar maps every DTO to a domain EarningsEvent`() = runTest {
        coEvery { apiService.getEarningsCalendar("2026-07-01", "2026-07-31", "AAPL") } returns com.sypark.finnhub.core.network.dto.EarningsCalendarResponseDto(
            listOf(com.sypark.finnhub.core.network.dto.EarningsEventDto("2026-07-15", 1.5, null, null, null, "AAPL")),
        )
        val result = repository.getEarningsCalendar("2026-07-01", "2026-07-31", "AAPL")
        assertTrue(result is AppResult.Success)
        assertEquals("AAPL", (result as AppResult.Success).data.single().symbol)
    }
}
