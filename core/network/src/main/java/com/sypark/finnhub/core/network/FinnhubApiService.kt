package com.sypark.finnhub.core.network

import com.sypark.finnhub.core.network.dto.QuoteDto
import com.sypark.finnhub.core.network.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApiService {
    @GET("quote")
    suspend fun getQuote(@Query("symbol") symbol: String): QuoteDto

    @GET("search")
    suspend fun search(@Query("q") query: String): SearchResponseDto
}
