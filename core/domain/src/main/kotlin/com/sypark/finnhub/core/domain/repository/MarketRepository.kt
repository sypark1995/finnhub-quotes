package com.sypark.finnhub.core.domain.repository

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.Candle
import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.domain.model.EarningsEvent
import com.sypark.finnhub.core.domain.model.News
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.domain.model.StockMetrics
import com.sypark.finnhub.core.domain.model.StockProfile
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    fun observeQuotes(symbols: Set<String>): Flow<Map<String, Quote>>
    fun observeConnectionStatus(): Flow<ConnectionStatus>
    suspend fun disconnect()
    suspend fun getQuote(symbol: String): AppResult<Quote>
    suspend fun search(query: String): AppResult<List<SearchResult>>
    suspend fun getCandles(symbol: String, resolution: String, from: Long, to: Long): AppResult<List<Candle>>
    suspend fun getStockProfile(symbol: String): AppResult<StockProfile>
    suspend fun getStockMetrics(symbol: String): AppResult<StockMetrics>
    suspend fun getPeers(symbol: String): AppResult<List<String>>
    suspend fun getCompanyNews(symbol: String, from: String, to: String): AppResult<List<News>>
    suspend fun getEarningsCalendar(from: String, to: String, symbol: String?): AppResult<List<EarningsEvent>>
}
