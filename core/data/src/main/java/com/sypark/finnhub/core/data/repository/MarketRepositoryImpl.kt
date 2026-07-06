package com.sypark.finnhub.core.data.repository

import com.sypark.finnhub.core.common.AppDispatchers
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.data.mapper.mapNetworkError
import com.sypark.finnhub.core.data.mapper.toCacheEntity
import com.sypark.finnhub.core.data.mapper.toDomain
import com.sypark.finnhub.core.data.util.CacheTtl
import com.sypark.finnhub.core.database.dao.CandleCacheDao
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
    private val candleCacheDao: CandleCacheDao,
    private val dispatchers: AppDispatchers,
    private val nowProvider: () -> Long = { System.currentTimeMillis() },
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

    override suspend fun getCandles(symbol: String, resolution: String, from: Long, to: Long): AppResult<List<Candle>> =
        withContext(dispatchers.io) {
            val now = nowProvider()
            val lastFetchedAt = candleCacheDao.getLatestFetchedAt(symbol, resolution)
            if (CacheTtl.isFresh(lastFetchedAt, now, CacheTtl.CANDLE_TTL_MILLIS)) {
                return@withContext AppResult.Success(candleCacheDao.getCandles(symbol, resolution).map { it.toDomain() })
            }
            try {
                val isForex = symbol.contains(":") && symbol.contains("_")
                val dto = if (isForex) {
                    apiService.getForexCandles(symbol, resolution, from, to)
                } else {
                    apiService.getStockCandles(symbol, resolution, from, to)
                }
                val candles = dto.toDomain()
                candleCacheDao.insertAll(candles.map { it.toCacheEntity(symbol, resolution, now) })
                AppResult.Success(candles)
            } catch (throwable: Throwable) {
                val cached = candleCacheDao.getCandles(symbol, resolution).map { it.toDomain() }
                if (cached.isNotEmpty()) AppResult.Success(cached) else AppResult.Error(mapNetworkError(throwable))
            }
        }

    override suspend fun getStockProfile(symbol: String): AppResult<StockProfile> = withContext(dispatchers.io) {
        try {
            AppResult.Success(apiService.getStockProfile(symbol).toDomain(symbol))
        } catch (throwable: Throwable) {
            AppResult.Error(mapNetworkError(throwable))
        }
    }

    override suspend fun getStockMetrics(symbol: String): AppResult<StockMetrics> = withContext(dispatchers.io) {
        try {
            AppResult.Success(apiService.getStockMetrics(symbol).toDomain(symbol))
        } catch (throwable: Throwable) {
            AppResult.Error(mapNetworkError(throwable))
        }
    }

    override suspend fun getPeers(symbol: String): AppResult<List<String>> = withContext(dispatchers.io) {
        try {
            AppResult.Success(apiService.getPeers(symbol))
        } catch (throwable: Throwable) {
            AppResult.Error(mapNetworkError(throwable))
        }
    }

    override suspend fun getCompanyNews(symbol: String, from: String, to: String): AppResult<List<News>> = withContext(dispatchers.io) {
        try {
            AppResult.Success(apiService.getCompanyNews(symbol, from, to).map { it.toDomain() })
        } catch (throwable: Throwable) {
            AppResult.Error(mapNetworkError(throwable))
        }
    }

    override suspend fun getEarningsCalendar(from: String, to: String, symbol: String?): AppResult<List<EarningsEvent>> = withContext(dispatchers.io) {
        try {
            AppResult.Success(apiService.getEarningsCalendar(from, to, symbol).earningsCalendar.map { it.toDomain() })
        } catch (throwable: Throwable) {
            AppResult.Error(mapNetworkError(throwable))
        }
    }
}
