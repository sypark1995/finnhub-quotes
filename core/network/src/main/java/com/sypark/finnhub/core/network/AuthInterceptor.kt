package com.sypark.finnhub.core.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val urlWithToken = originalRequest.url.newBuilder()
            .addQueryParameter("token", BuildConfig.FINNHUB_API_KEY)
            .build()
        val requestWithToken = originalRequest.newBuilder()
            .url(urlWithToken)
            .build()
        return chain.proceed(requestWithToken)
    }
}
