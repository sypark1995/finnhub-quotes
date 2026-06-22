package com.sypark.finnhub.core.websocket

import com.sypark.finnhub.core.common.AppCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.set

interface FinnhubWebSocketManager {
    val connectionState: StateFlow<ConnectionState>
    val tradeUpdates: SharedFlow<TradeMessage>

    suspend fun connect()
    suspend fun disconnect()
    suspend fun subscribe(symbols: Set<String>)
    suspend fun unsubscribe(symbols: Set<String>)

    /** Not in design.md §7.1's literal interface — added and documented in Task 12's header. */
    suspend fun syncSubscriptions(desiredSymbols: Set<String>)
}

@Serializable
private data class SubscriptionMessage(val type: String, val symbol: String)

@Singleton
class FinnhubWebSocketManagerImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    private val appCoroutineScope: AppCoroutineScope,
    private val webSocketUrl: String,
) : FinnhubWebSocketManager {

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _tradeUpdates = MutableSharedFlow<TradeMessage>(extraBufferCapacity = 64)
    override val tradeUpdates: SharedFlow<TradeMessage> = _tradeUpdates.asSharedFlow()

    private val subscriptionRegistry = SubscriptionRegistry()
    private var webSocket: WebSocket? = null
    private var reconnectAttempt = 0
    private var userInitiatedDisconnect = false

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            reconnectAttempt = 0
            _connectionState.value = ConnectionState.Connected
            // design.md §7.4: resend the full active subscription set on every (re)connect.
            subscriptionRegistry.currentSymbols().forEach { symbol -> sendSubscribe(webSocket, symbol) }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val element = runCatching { json.parseToJsonElement(text).jsonObject }.getOrNull() ?: return
            when (element["type"]?.jsonPrimitive?.contentOrNull) {
                "trade" -> parseTrades(element["data"]).forEach { trade -> _tradeUpdates.tryEmit(trade) }
                "ping" -> { /* liveness only, no pong required (design.md §7.4) */ }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (!userInitiatedDisconnect) scheduleReconnect()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (!userInitiatedDisconnect) scheduleReconnect()
        }
    }

    override suspend fun connect() {
        userInitiatedDisconnect = false
        _connectionState.value = ConnectionState.Connecting
        webSocket = okHttpClient.newWebSocket(Request.Builder().url(webSocketUrl).build(), listener)
    }

    override suspend fun disconnect() {
        userInitiatedDisconnect = true
        webSocket?.close(1000, "client disconnect")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    override suspend fun subscribe(symbols: Set<String>) {
        val socket = webSocket ?: return
        symbols.forEach { symbol -> sendSubscribe(socket, symbol) }
    }

    override suspend fun unsubscribe(symbols: Set<String>) {
        val socket = webSocket ?: return
        symbols.forEach { symbol -> socket.send(json.encodeToString(SubscriptionMessage("unsubscribe", symbol))) }
    }

    override suspend fun syncSubscriptions(desiredSymbols: Set<String>) {
        val diff = subscriptionRegistry.diffAndUpdate(desiredSymbols)
        if (diff.toAdd.isNotEmpty()) subscribe(diff.toAdd)
        if (diff.toRemove.isNotEmpty()) unsubscribe(diff.toRemove)
    }

    private fun sendSubscribe(socket: WebSocket, symbol: String) {
        socket.send(json.encodeToString(SubscriptionMessage("subscribe", symbol)))
    }

    private fun parseTrades(dataElement: JsonElement?): List<TradeMessage> {
        val array = dataElement as? kotlinx.serialization.json.JsonArray ?: return emptyList()
        return array.mapNotNull { item ->
            val obj = item.jsonObject
            val symbol = obj["s"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val price = obj["p"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: return@mapNotNull null
            val volume = obj["v"]?.jsonPrimitive?.content?.toDoubleOrNull()?.toLong() ?: 0L
            val timestamp = obj["t"]?.jsonPrimitive?.content?.toDoubleOrNull()?.toLong() ?: 0L
            TradeMessage(symbol, price, volume, timestamp)
        }
    }

    private fun scheduleReconnect() {
        _connectionState.value = ConnectionState.Reconnecting
        val delayMillis = nextBackoffDelayMillis(reconnectAttempt)
        reconnectAttempt++
        appCoroutineScope.launch {
            kotlinx.coroutines.delay(delayMillis)
            if (!userInitiatedDisconnect) connect()
        }
    }
}
