package com.sypark.finnhub.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "candle_cache",
    indices = [Index(value = ["symbol", "resolution", "timestamp"], unique = true)],
)
data class CandleCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val resolution: String,
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val fetchedAt: Long,
)
