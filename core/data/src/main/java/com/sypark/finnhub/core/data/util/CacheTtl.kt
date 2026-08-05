package com.sypark.finnhub.core.data.util

object CacheTtl {
    const val QUOTE_TTL_MILLIS: Long = 5 * 60 * 1000L

    // Earnings estimates are quarterly and rarely change within a day, unlike quotes -- a much
    // longer TTL is fine and meaningfully cuts down on repeat calendar/earnings calls.
    const val EARNINGS_TTL_MILLIS: Long = 24 * 60 * 60 * 1000L

    fun isFresh(lastFetchedAt: Long?, now: Long, ttlMillis: Long): Boolean =
        lastFetchedAt != null && (now - lastFetchedAt) < ttlMillis
}
