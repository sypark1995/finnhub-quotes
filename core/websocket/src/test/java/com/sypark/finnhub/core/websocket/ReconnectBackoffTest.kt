package com.sypark.finnhub.core.websocket

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReconnectBackoffTest {

    @Test
    fun `delay doubles from 1s starting at attempt 0`() {
        assertEquals(1_000L, nextBackoffDelayMillis(attempt = 0))
        assertEquals(2_000L, nextBackoffDelayMillis(attempt = 1))
        assertEquals(4_000L, nextBackoffDelayMillis(attempt = 2))
        assertEquals(8_000L, nextBackoffDelayMillis(attempt = 3))
    }

    @Test
    fun `delay caps at 30s for large attempt counts`() {
        assertEquals(30_000L, nextBackoffDelayMillis(attempt = 10))
        assertEquals(30_000L, nextBackoffDelayMillis(attempt = 100))
    }
}
