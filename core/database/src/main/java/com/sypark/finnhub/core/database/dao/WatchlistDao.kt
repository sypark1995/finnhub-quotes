package com.sypark.finnhub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sypark.finnhub.core.database.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE symbol = :symbol")
    suspend fun delete(symbol: String)

    @Query("UPDATE watchlist SET sortOrder = :sortOrder WHERE symbol = :symbol")
    suspend fun updateSortOrder(symbol: String, sortOrder: Int)

    // Single transaction so observeAll() emits once for the whole reorder instead of once
    // per row: N separate UPDATEs each trigger their own Room invalidation, and callers that
    // flatMapLatest off observeAll() (e.g. WatchlistViewModel) end up rapidly cancelling and
    // restarting their downstream subscription N times, which can leave the final resubscription
    // never producing its first emission.
    @Transaction
    suspend fun updateSortOrders(symbols: List<String>, sortOrders: List<Int>) {
        symbols.forEachIndexed { index, symbol -> updateSortOrder(symbol, sortOrders[index]) }
    }

    @Query("SELECT * FROM watchlist WHERE symbol = :symbol LIMIT 1")
    suspend fun getBySymbol(symbol: String): WatchlistEntity?
}
