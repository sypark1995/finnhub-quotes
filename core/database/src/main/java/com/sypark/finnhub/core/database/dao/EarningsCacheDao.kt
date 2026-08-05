package com.sypark.finnhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sypark.finnhub.core.database.entity.EarningsCacheEntity

@Dao
interface EarningsCacheDao {
    @Query("SELECT * FROM earnings_cache WHERE symbol = :symbol ORDER BY date ASC")
    suspend fun getForSymbol(symbol: String): List<EarningsCacheEntity>

    @Query("SELECT MAX(fetchedAt) FROM earnings_cache WHERE symbol = :symbol")
    suspend fun getLatestFetchedAt(symbol: String): Long?

    @Query("DELETE FROM earnings_cache WHERE symbol = :symbol")
    suspend fun deleteForSymbol(symbol: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<EarningsCacheEntity>)

    // Finnhub's response for a symbol can drop or shift rows between fetches (estimates get
    // refined), so a fresh fetch replaces the whole set for that symbol rather than upserting
    // on top of possibly-stale rows.
    @Transaction
    suspend fun replaceForSymbol(symbol: String, entities: List<EarningsCacheEntity>) {
        deleteForSymbol(symbol)
        insertAll(entities)
    }
}
