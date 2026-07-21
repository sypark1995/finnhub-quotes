package com.sypark.finnhub.core.network

import okhttp3.Interceptor
import okhttp3.Response

private const val MAX_RETRY_DELAY_SECONDS = 10L

class RateLimitInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code != 429) return response

        val retryAfterSeconds = response.header("Retry-After")?.toLongOrNull()?.coerceAtMost(MAX_RETRY_DELAY_SECONDS) ?: 2L
        response.close()
        Thread.sleep(retryAfterSeconds * 1000)
        return chain.proceed(request)
    }
}
