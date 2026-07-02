package com.sypark.finnhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sypark.finnhub.core.database.entity.CandleCacheEntity

@Dao
interface CandleCacheDao {
    @Query("SELECT * FROM candle_cache WHERE symbol = :symbol AND resolution = :resolution ORDER BY timestamp ASC")
    suspend fun getCandles(symbol: String, resolution: String): List<CandleCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CandleCacheEntity>)

    @Query("DELETE FROM candle_cache WHERE fetchedAt < :thresholdMillis")
    suspend fun deleteOlderThan(thresholdMillis: Long)

    @Query("SELECT MAX(fetchedAt) FROM candle_cache WHERE symbol = :symbol AND resolution = :resolution")
    suspend fun getLatestFetchedAt(symbol: String, resolution: String): Long?
}
