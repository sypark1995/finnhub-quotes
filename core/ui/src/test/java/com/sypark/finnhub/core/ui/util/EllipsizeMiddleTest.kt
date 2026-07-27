package com.sypark.finnhub.core.ui.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EllipsizeMiddleTest {

    @Test
    fun `strings within the max length are returned unchanged`() {
        assertEquals("AAPL", ellipsizeMiddle("AAPL", maxLength = 10))
    }

    @Test
    fun `strings over the max length are truncated in the middle with an ellipsis`() {
        val result = ellipsizeMiddle("OANDA:EUR_USD", maxLength = 9)
        assertEquals(9, result.length)
        assertEquals(true, result.contains("…"))
        assertEquals(true, result.startsWith("OAN"))
        assertEquals(true, result.endsWith("USD"))
    }
}
