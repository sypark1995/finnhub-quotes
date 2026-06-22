// core/database/src/test/java/com/sypark/finnhub/core/database/ConvertersTest.kt
package com.sypark.finnhub.core.database

import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AssetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `AssetType round trips through its string representation`() {
        AssetType.entries.forEach { type ->
            val stored = converters.fromAssetType(type)
            assertEquals(type, converters.toAssetType(stored))
        }
    }

    @Test
    fun `AlertCondition round trips through its string representation`() {
        AlertCondition.entries.forEach { condition ->
            val stored = converters.fromAlertCondition(condition)
            assertEquals(condition, converters.toAlertCondition(stored))
        }
    }
}
