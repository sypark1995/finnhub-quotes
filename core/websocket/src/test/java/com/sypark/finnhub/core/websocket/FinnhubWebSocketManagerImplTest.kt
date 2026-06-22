package com.sypark.finnhub.core.websocket

import app.cash.turbine.test
import com.sypark.finnhub.core.common.AppCoroutineScope
import com.sypark.finnhub.core.common.AppDispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class FinnhubWebSocketManagerImplTest {

    private lateinit var server: MockWebServer
    private lateinit var manager: FinnhubWebSocketManagerImpl

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
        val client = OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS).build()
        manager = FinnhubWebSocketManagerImpl(
            okHttpClient = client,
            json = Json { ignoreUnknownKeys = true },
            appCoroutineScope = AppCoroutineScope(AppDispatchers()),
            webSocketUrl = server.url("/").toString().replace("http://", "ws://"),
        )
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `connect transitions state to Connected once the server accepts the upgrade`() = runTest {
        server.enqueue(MockResponse().withWebSocketUpgrade(object : WebSocketListener() {}))

        manager.connectionState.test {
            assertEquals(ConnectionState.Disconnected, awaitItem())
            manager.connect()
            assertEquals(ConnectionState.Connecting, awaitItem())
            assertEquals(ConnectionState.Connected, awaitItem())
        }
    }

    @Test
    fun `a trade message from the server is parsed and emitted on tradeUpdates`() = runTest {
        lateinit var serverSocket: WebSocket
        server.enqueue(
            MockResponse().withWebSocketUpgrade(
                object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                        serverSocket = webSocket
                    }
                },
            ),
        )

        manager.connect()
        // The server-side handshake (which assigns serverSocket) and the client-side
        // Connected transition race on real background threads; wait for the state we
        // already proved is reachable in the test above before touching serverSocket.
        manager.connectionState.first { it == ConnectionState.Connected }

        manager.tradeUpdates.test {
            serverSocket.send("""{"type":"trade","data":[{"s":"AAPL","p":198.5,"v":10,"t":1720000000000}]}""")
            val trade = awaitItem()
            assertEquals(TradeMessage(symbol = "AAPL", price = 198.5, volume = 10, timestamp = 1720000000000), trade)
        }
    }
}
