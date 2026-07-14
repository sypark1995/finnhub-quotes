package com.sypark.finnhub.core.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RateLimitInterceptorTest {

    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() { server = MockWebServer(); server.start() }

    @AfterEach
    fun tearDown() = server.shutdown()

    @Test
    fun `a 429 followed by a 200 retries once and returns the 200`() {
        server.enqueue(MockResponse().setResponseCode(429).setHeader("Retry-After", "0"))
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))
        val client = OkHttpClient.Builder().addInterceptor(RateLimitInterceptor()).build()

        val response = client.newCall(Request.Builder().url(server.url("/quote")).build()).execute()

        assertEquals(200, response.code)
        assertEquals(2, server.requestCount)
    }
}
