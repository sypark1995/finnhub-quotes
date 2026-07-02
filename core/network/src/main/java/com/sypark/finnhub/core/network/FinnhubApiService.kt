package com.sypark.finnhub.core.network

import com.sypark.finnhub.core.network.dto.CandleResponseDto
import com.sypark.finnhub.core.network.dto.CompanyNewsDto
import com.sypark.finnhub.core.network.dto.QuoteDto
import com.sypark.finnhub.core.network.dto.SearchResponseDto
import com.sypark.finnhub.core.network.dto.StockMetricResponseDto
import com.sypark.finnhub.core.network.dto.StockProfileDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApiService {
    @GET("quote")
    suspend fun getQuote(@Query("symbol") symbol: String): QuoteDto

    @GET("search")
    suspend fun search(@Query("q") query: String): SearchResponseDto

    @GET("stock/candle")
    suspend fun getStockCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
    ): CandleResponseDto

    @GET("forex/candle")
    suspend fun getForexCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
    ): CandleResponseDto

    @GET("stock/profile2")
    suspend fun getStockProfile(@Query("symbol") symbol: String): StockProfileDto

    @GET("stock/metric")
    suspend fun getStockMetrics(@Query("symbol") symbol: String, @Query("metric") metric: String = "all"): StockMetricResponseDto

    @GET("stock/peers")
    suspend fun getPeers(@Query("symbol") symbol: String): List<String>

    @GET("company-news")
    suspend fun getCompanyNews(
        @Query("symbol") symbol: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): List<CompanyNewsDto>
}
