package com.sypark.finnhub.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.URLDecoder
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

    @Test
    fun `search parses Finnhub's result-array JSON`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{"count":1,"result":[{"description":"APPLE INC","displaySymbol":"AAPL","symbol":"AAPL","type":"Common Stock"}]}""",
            ),
        )

        val response = service.search(query = "AAPL")

        assertEquals(1, response.count)
        assertEquals("AAPL", response.result.single().symbol)
        assertEquals("/search?q=AAPL", server.takeRequest().path)
    }

    @Test
    fun `getStockCandles parses Finnhub's parallel-array OHLCV JSON`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{"c":[198.5],"h":[199.1],"l":[196.8],"o":[197.2],"s":"ok","t":[1720000000],"v":[1000]}""",
            ),
        )
        val dto = service.getStockCandles(symbol = "AAPL", resolution = "D", from = 1, to = 2)
        assertEquals("ok", dto.s)
        assertEquals(198.5, dto.c.single())
    }

    @Test
    fun `getForexCandles hits the forex candle path`() = runTest {
        server.enqueue(MockResponse().setBody("""{"c":[],"h":[],"l":[],"o":[],"s":"no_data","t":[],"v":[]}"""))
        service.getForexCandles(symbol = "OANDA:EUR_USD", resolution = "D", from = 1, to = 2)
        assertEquals("/forex/candle?symbol=OANDA:EUR_USD&resolution=D&from=1&to=2", URLDecoder.decode(server.takeRequest().path, "UTF-8"))
    }

    @Test
    fun `getStockProfile parses Finnhub's profile2 JSON`() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{"name":"Apple Inc","exchange":"NASDAQ","finnhubIndustry":"Technology","logo":"https://x/logo.png","marketCapitalization":3010000.0,"weburl":"https://apple.com","currency":"USD"}""",
            ),
        )
        val dto = service.getStockProfile(symbol = "AAPL")
        assertEquals("Apple Inc", dto.name)
        assertEquals(3010000.0, dto.marketCapitalization)
    }
}
