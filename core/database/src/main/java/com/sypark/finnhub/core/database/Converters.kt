package com.sypark.finnhub.core.database

import androidx.room.TypeConverter
import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AssetType

class Converters {
    @TypeConverter
    fun fromAssetType(value: AssetType): String = value.name

    @TypeConverter
    fun toAssetType(value: String): AssetType = AssetType.valueOf(value)

    @TypeConverter
    fun fromAlertCondition(value: AlertCondition): String = value.name

    @TypeConverter
    fun toAlertCondition(value: String): AlertCondition = AlertCondition.valueOf(value)
}
