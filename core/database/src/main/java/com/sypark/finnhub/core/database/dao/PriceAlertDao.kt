package com.sypark.finnhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sypark.finnhub.core.database.entity.PriceAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alert ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alert WHERE isEnabled = 1 AND triggeredAt IS NULL")
    fun observeEnabled(): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alert WHERE symbol = :symbol")
    fun observeForSymbol(symbol: String): Flow<List<PriceAlertEntity>>

    @Insert
    suspend fun insert(entity: PriceAlertEntity): Long

    @Update
    suspend fun update(entity: PriceAlertEntity)

    @Query("DELETE FROM price_alert WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE price_alert SET triggeredAt = :triggeredAt WHERE id = :id")
    suspend fun markTriggered(id: Long, triggeredAt: Long)
}
