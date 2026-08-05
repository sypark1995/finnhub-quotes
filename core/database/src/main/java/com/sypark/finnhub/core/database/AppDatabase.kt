package com.sypark.finnhub.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sypark.finnhub.core.database.dao.EarningsCacheDao
import com.sypark.finnhub.core.database.dao.PriceAlertDao
import com.sypark.finnhub.core.database.dao.QuoteCacheDao
import com.sypark.finnhub.core.database.dao.WatchlistDao
import com.sypark.finnhub.core.database.entity.EarningsCacheEntity
import com.sypark.finnhub.core.database.entity.PriceAlertEntity
import com.sypark.finnhub.core.database.entity.QuoteCacheEntity
import com.sypark.finnhub.core.database.entity.WatchlistEntity

@Database(
    entities = [WatchlistEntity::class, QuoteCacheEntity::class, PriceAlertEntity::class, EarningsCacheEntity::class],
    version = 6,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun quoteCacheDao(): QuoteCacheDao
    abstract fun priceAlertDao(): PriceAlertDao
    abstract fun earningsCacheDao(): EarningsCacheDao
}
