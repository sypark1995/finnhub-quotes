package com.sypark.finnhub.core.websocket

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SubscriptionRegistryTest {

    @Test
    fun `first sync adds every symbol and removes none`() {
        val registry = SubscriptionRegistry()
        val diff = registry.diffAndUpdate(setOf("AAPL", "MSFT"))
        assertEquals(setOf("AAPL", "MSFT"), diff.toAdd)
        assertEquals(emptySet<String>(), diff.toRemove)
    }

    @Test
    fun `second sync computes add-remove diff against the previous set`() {
        val registry = SubscriptionRegistry()
        registry.diffAndUpdate(setOf("AAPL", "MSFT"))

        val diff = registry.diffAndUpdate(setOf("MSFT", "TSLA"))

        assertEquals(setOf("TSLA"), diff.toAdd)
        assertEquals(setOf("AAPL"), diff.toRemove)
    }

    @Test
    fun `currentSymbols reflects the latest synced set`() {
        val registry = SubscriptionRegistry()
        registry.diffAndUpdate(setOf("AAPL"))
        assertEquals(setOf("AAPL"), registry.currentSymbols())
    }
}
