package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.database.entity.PriceAlertEntity
import com.sypark.finnhub.core.domain.model.PriceAlert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PriceAlertMapperTest {

    @Test
    fun `toDomain drops createdAt and keeps every other field`() {
        val entity = PriceAlertEntity(id = 1, symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE, isEnabled = true, triggeredAt = null, createdAt = 999L)
        assertEquals(PriceAlert(1, "AAPL", 210.0, AlertCondition.ABOVE, true, null), entity.toDomain())
    }

    @Test
    fun `toEntity attaches the given createdAt`() {
        val alert = PriceAlert(0, "AAPL", 210.0, AlertCondition.ABOVE, true, null)
        assertEquals(PriceAlertEntity(id = 0, symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE, isEnabled = true, triggeredAt = null, createdAt = 999L), alert.toEntity(createdAt = 999L))
    }
}
