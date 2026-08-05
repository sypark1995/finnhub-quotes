package com.sypark.finnhub.core.database.entity

import androidx.room.Entity

@Entity(tableName = "earnings_cache", primaryKeys = ["symbol", "date"])
data class EarningsCacheEntity(
    val symbol: String,
    val date: String,
    val hour: String,
    val epsEstimate: Double?,
    val epsActual: Double?,
    val revenueEstimate: Double?,
    val revenueActual: Double?,
    val fetchedAt: Long,
)
