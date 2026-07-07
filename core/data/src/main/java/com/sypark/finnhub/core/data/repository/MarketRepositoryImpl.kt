package com.sypark.finnhub.core.data.repository

import com.sypark.finnhub.core.common.AppDispatchers
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.data.mapper.mapNetworkError
import com.sypark.finnhub.core.data.mapper.toCacheEntity
import com.sypark.finnhub.core.data.mapper.toDomain
import com.sypark.finnhub.core.database.dao.QuoteCacheDao
import com.sypark.finnhub.core.domain.model.Candle
import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.domain.model.EarningsEvent
import com.sypark.finnhub.core.domain.model.News
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.domain.model.StockMetrics
import com.sypark.finnhub.core.domain.model.StockProfile
import com.sypark.finnhub.core.domain.repository.MarketRepository
import com.sypark.finnhub.core.network.FinnhubApiService
import com.sypark.finnhub.core.websocket.ConnectionState
import com.sypark.finnhub.core.websocket.FinnhubWebSocketManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val apiService: FinnhubApiService,
    private val webSocketManager: FinnhubWebSocketManager,
    private val quoteCacheDao: QuoteCacheDao,
    private val dispatchers: AppDispatchers,
) : MarketRepository {

    override fun observeQuotes(symbols: Set<String>): Flow<Map<String, Quote>> = callbackFlow {
        if (symbols.isEmpty()) {
            trySend(emptyMap())
            awaitClose { }
            return@callbackFlow
        }
        val currentQuotes = mutableMapOf<String, Quote>()
        webSocketManager.connect()
        webSocketManager.syncSubscriptions(symbols)

        val cacheJob = launch(dispatchers.io) {
            quoteCacheDao.observeAll().collect { cached ->
                cached.filter { it.symbol in symbols }.forEach { entity ->
                    if (currentQuotes[entity.symbol] == null) currentQuotes[entity.symbol] = entity.toDomain()
                }
                trySend(currentQuotes.toMap())
            }
        }
        val tradeJob = launch(dispatchers.io) {
            webSocketManager.tradeUpdates.collect { trade ->
                if (trade.symbol !in symbols) return@collect
                val updated = trade.toDomain(previous = currentQuotes[trade.symbol])
                currentQuotes[trade.symbol] = updated
                trySend(currentQuotes.toMap())
                quoteCacheDao.upsert(updated.toCacheEntity())
            }
        }
        awaitClose {
            cacheJob.cancel()
            tradeJob.cancel()
        }
    }.flowOn(dispatchers.io)

    override fun observeConnectionStatus(): Flow<ConnectionStatus> = webSocketManager.connectionState.map { state ->
        when (state) {
            ConnectionState.Connected -> ConnectionStatus.Connected
            ConnectionState.Connecting -> ConnectionStatus.Connecting
            ConnectionState.Reconnecting -> ConnectionStatus.Reconnecting
            ConnectionState.Disconnected -> ConnectionStatus.Disconnected
        }
    }

    override suspend fun getQuote(symbol: String): AppResult<Quote> = withContext(dispatchers.io) {
        try {
            val quote = apiService.getQuote(symbol).toDomain(symbol)
            quoteCacheDao.upsert(quote.toCacheEntity())
            AppResult.Success(quote)
        } catch (throwable: Throwable) {
            val cached = quoteCacheDao.observe(symbol).first()
            if (cached != null) AppResult.Success(cached.toDomain()) else AppResult.Error(mapNetworkError(throwable))
        }
    }

    override suspend fun search(query: String): AppResult<List<SearchResult>> = withContext(dispatchers.io) {
        try {
            AppResult.Success(apiService.search(query).result.map { it.toDomain() })
        } catch (throwable: Throwable) {
            AppResult.Error(mapNetworkError(throwable))
        }
    }

    // getCandles / getStockProfile / getStockMetrics / getPeers / getCompanyNews /
    // getEarningsCalendar are implemented across Tasks 37–40 as modifications to this class.

    override suspend fun getCandles(symbol: String, resolution: String, from: Long, to: Long): AppResult<List<Candle>> =
        throw NotImplementedError("getCandles() is implemented in Task 26")

    override suspend fun getStockProfile(symbol: String): AppResult<StockProfile> =
        throw NotImplementedError("getStockProfile() is implemented in Task 27")

    override suspend fun getStockMetrics(symbol: String): AppResult<StockMetrics> =
        throw NotImplementedError("getStockMetrics() is implemented in Task 27")

    override suspend fun getPeers(symbol: String): AppResult<List<String>> =
        throw NotImplementedError("getPeers() is implemented in Task 27")

    override suspend fun getCompanyNews(symbol: String, from: String, to: String): AppResult<List<News>> =
        throw NotImplementedError("getCompanyNews() is implemented in Task 27")

    override suspend fun getEarningsCalendar(from: String, to: String, symbol: String?): AppResult<List<EarningsEvent>> =
        throw NotImplementedError("getEarningsCalendar() is implemented in Task 27")
}
