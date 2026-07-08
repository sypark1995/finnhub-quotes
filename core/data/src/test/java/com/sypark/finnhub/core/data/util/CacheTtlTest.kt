package com.sypark.finnhub.core.data.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CacheTtlTest {

    @Test
    fun `isFresh is false when nothing was ever fetched`() {
        assertFalse(CacheTtl.isFresh(lastFetchedAt = null, now = 1_000L, ttlMillis = 500L))
    }

    @Test
    fun `isFresh is true within the TTL window`() {
        assertTrue(CacheTtl.isFresh(lastFetchedAt = 1_000L, now = 1_400L, ttlMillis = 500L))
    }

    @Test
    fun `isFresh is false once the TTL window has passed`() {
        assertFalse(CacheTtl.isFresh(lastFetchedAt = 1_000L, now = 1_600L, ttlMillis = 500L))
    }
}
