package com.sypark.finnhub.core.data.util

object CacheTtl {
    const val QUOTE_TTL_MILLIS: Long = 5 * 60 * 1000L

    fun isFresh(lastFetchedAt: Long?, now: Long, ttlMillis: Long): Boolean =
        lastFetchedAt != null && (now - lastFetchedAt) < ttlMillis
}
