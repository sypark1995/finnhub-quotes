package com.sypark.finnhub.core.ui.util

import com.sypark.finnhub.core.ui.model.ChangeDirection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormattersTest {

    @Test
    fun `formatPrice formats as USD with 2 decimals`() {
        assertEquals("$198.50", formatPrice(198.5))
        assertEquals("$65,432.10", formatPrice(65432.10))
    }

    @Test
    fun `formatPercent always includes a leading sign`() {
        assertEquals("+1.17%", formatPercent(1.17))
        assertEquals("-0.32%", formatPercent(-0.32))
        assertEquals("+0.00%", formatPercent(0.0))
    }

    @Test
    fun `formatLargeNumber abbreviates trillions and billions`() {
        assertEquals("1.2T", formatLargeNumber(1_200_000_000_000.0))
        assertEquals("850.3B", formatLargeNumber(850_300_000_000.0))
        assertEquals("42.0M", formatLargeNumber(42_000_000.0))
    }

    @Test
    fun `changeDirectionOf maps sign of change percent to ChangeDirection`() {
        assertEquals(ChangeDirection.UP, changeDirectionOf(1.17))
        assertEquals(ChangeDirection.DOWN, changeDirectionOf(-0.32))
        assertEquals(ChangeDirection.FLAT, changeDirectionOf(0.0))
    }
}
