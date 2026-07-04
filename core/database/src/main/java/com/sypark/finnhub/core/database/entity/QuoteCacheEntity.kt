package com.sypark.finnhub.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quote_cache")
data class QuoteCacheEntity(
    @PrimaryKey val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val previousClose: Double,
    val updatedAt: Long,
)
