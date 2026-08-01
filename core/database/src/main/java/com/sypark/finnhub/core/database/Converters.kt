package com.sypark.finnhub.core.database

import androidx.room.TypeConverter
import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AssetType

class Converters {
    @TypeConverter
    fun fromAssetType(value: AssetType): String = value.name

    @TypeConverter
    fun toAssetType(value: String): AssetType =
        // A row saved before FOREX support was removed could still have assetType="FOREX" on
        // disk (the column is a plain TEXT column, so no schema migration would catch this) --
        // fall back to STOCK rather than crash on an enum value that no longer exists.
        runCatching { AssetType.valueOf(value) }.getOrDefault(AssetType.STOCK)

    @TypeConverter
    fun fromAlertCondition(value: AlertCondition): String = value.name

    @TypeConverter
    fun toAlertCondition(value: String): AlertCondition = AlertCondition.valueOf(value)
}
