package com.sypark.finnhub.core.websocket

import kotlin.math.min
import kotlin.math.pow

private const val BASE_DELAY_MILLIS = 1_000L
private const val MAX_DELAY_MILLIS = 30_000L

/**
 * Exponential backoff: 1s → 2s → 4s → … capped at 30s (design.md §7.4).
 *
 * The cap is applied in Double space before the single final `toLong()` conversion.
 * Doing the multiply in Long space first (`BASE_DELAY_MILLIS * 2.0.pow(attempt).toLong()`)
 * overflows for large attempt counts: `2.0.pow(100).toLong()` saturates to `Long.MAX_VALUE`,
 * and `1_000L * Long.MAX_VALUE` then wraps around to a negative number, so the cap never
 * kicks in. Capping in Double space first avoids the overflow entirely.
 */
fun nextBackoffDelayMillis(attempt: Int): Long =
    min(BASE_DELAY_MILLIS.toDouble() * 2.0.pow(attempt), MAX_DELAY_MILLIS.toDouble()).toLong()
