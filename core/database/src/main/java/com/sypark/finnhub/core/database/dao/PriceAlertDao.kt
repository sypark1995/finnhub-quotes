package com.sypark.finnhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sypark.finnhub.core.common.AlertCondition
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

    // Deliberately a targeted column update (not @Update on the full entity) so createdAt
    // is never touched — preserves original creation time for observeAll()'s ORDER BY.
    @Query("UPDATE price_alert SET targetPrice = :targetPrice, condition = :condition, isEnabled = :isEnabled WHERE id = :id")
    suspend fun update(id: Long, targetPrice: Double, condition: AlertCondition, isEnabled: Boolean)

    @Query("DELETE FROM price_alert WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE price_alert SET triggeredAt = :triggeredAt WHERE id = :id")
    suspend fun markTriggered(id: Long, triggeredAt: Long)
}
