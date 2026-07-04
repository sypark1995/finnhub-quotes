package com.sypark.finnhub.core.network

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AuthInterceptorTest {

    @Test
    fun `intercept appends token query parameter from BuildConfig without dropping existing params`() {
        val interceptor = AuthInterceptor()
        val originalRequest = Request.Builder()
            .url("https://finnhub.io/api/v1/quote?symbol=AAPL")
            .build()
        val chain = mockk<Interceptor.Chain>()
        val requestSlot = slot<Request>()
        val fakeResponse = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(requestSlot)) } returns fakeResponse

        interceptor.intercept(chain)

        assertEquals(BuildConfig.FINNHUB_API_KEY, requestSlot.captured.url.queryParameter("token"))
        assertEquals("AAPL", requestSlot.captured.url.queryParameter("symbol"))
    }
}
