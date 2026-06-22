package com.sypark.finnhub.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit

class FinnhubApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var service: FinnhubApiService

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        service = retrofit.create(FinnhubApiService::class.java)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getQuote parses Finnhub's short-key quote JSON`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{"c":198.5,"d":2.3,"dp":1.17,"h":199.1,"l":196.8,"o":197.2,"pc":196.2,"t":1720000000}""",
            ),
        )

        val dto = service.getQuote(symbol = "AAPL")

        assertEquals(198.5, dto.c)
        assertEquals(2.3, dto.d)
        assertEquals(1.17, dto.dp)
        assertEquals(1720000000L, dto.t)
        assertEquals("/quote?symbol=AAPL", server.takeRequest().path)
    }
}
