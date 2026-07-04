package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.database.entity.WatchlistEntity
import com.sypark.finnhub.core.domain.model.WatchlistItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WatchlistMapperTest {

    @Test
    fun `WatchlistEntity toDomain drops addedAt and keeps every other field`() {
        val entity = WatchlistEntity("AAPL", "Apple Inc.", AssetType.STOCK, sortOrder = 2, addedAt = 999L)
        assertEquals(WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, sortOrder = 2), entity.toDomain())
    }

    @Test
    fun `WatchlistItem toEntity attaches the given addedAt`() {
        val item = WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, sortOrder = 2)
        assertEquals(WatchlistEntity("AAPL", "Apple Inc.", AssetType.STOCK, sortOrder = 2, addedAt = 999L), item.toEntity(addedAt = 999L))
    }
}
