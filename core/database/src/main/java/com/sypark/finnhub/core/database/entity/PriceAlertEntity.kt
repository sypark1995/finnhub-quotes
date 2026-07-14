package com.sypark.finnhub.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sypark.finnhub.core.common.AlertCondition

@Entity(tableName = "price_alert")
data class PriceAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val targetPrice: Double,
    val condition: AlertCondition,
    val isEnabled: Boolean,
    val triggeredAt: Long? = null,
    val createdAt: Long,
)
