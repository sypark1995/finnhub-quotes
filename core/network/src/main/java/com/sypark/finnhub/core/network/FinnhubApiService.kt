package com.sypark.finnhub.core.network

import com.sypark.finnhub.core.network.dto.QuoteDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApiService {
    @GET("quote")
    suspend fun getQuote(@Query("symbol") symbol: String): QuoteDto
}
