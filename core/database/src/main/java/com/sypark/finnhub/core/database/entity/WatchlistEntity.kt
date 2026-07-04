package com.sypark.finnhub.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sypark.finnhub.core.common.AssetType

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val symbol: String,
    val displayName: String,
    val assetType: AssetType,
    val sortOrder: Int,
    val addedAt: Long,
)
