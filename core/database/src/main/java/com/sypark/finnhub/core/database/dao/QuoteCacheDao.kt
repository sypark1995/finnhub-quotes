package com.sypark.finnhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sypark.finnhub.core.database.entity.QuoteCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteCacheDao {
    @Query("SELECT * FROM quote_cache WHERE symbol = :symbol LIMIT 1")
    fun observe(symbol: String): Flow<QuoteCacheEntity?>

    @Query("SELECT * FROM quote_cache")
    fun observeAll(): Flow<List<QuoteCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: QuoteCacheEntity)
}
