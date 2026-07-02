package com.sypark.finnhub.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sypark.finnhub.core.database.AppDatabase
import com.sypark.finnhub.core.database.entity.CandleCacheEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CandleCacheDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: CandleCacheDao

    private fun candle(timestamp: Long, fetchedAt: Long) = CandleCacheEntity(
        symbol = "AAPL", resolution = "D", timestamp = timestamp,
        open = 197.2, high = 199.1, low = 196.8, close = 198.5, volume = 1000, fetchedAt = fetchedAt,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.candleCacheDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `insertAll then getCandles returns rows ordered by timestamp`() = runTest {
        dao.insertAll(listOf(candle(timestamp = 2L, fetchedAt = 1L), candle(timestamp = 1L, fetchedAt = 1L)))
        val result = dao.getCandles("AAPL", "D")
        assertEquals(listOf(1L, 2L), result.map { it.timestamp })
    }

    @Test
    fun `getLatestFetchedAt returns the max fetchedAt for the symbol+resolution pair`() = runTest {
        dao.insertAll(listOf(candle(timestamp = 1L, fetchedAt = 5L), candle(timestamp = 2L, fetchedAt = 9L)))
        assertEquals(9L, dao.getLatestFetchedAt("AAPL", "D"))
    }

    @Test
    fun `getLatestFetchedAt returns null when nothing is cached`() = runTest {
        assertNull(dao.getLatestFetchedAt("AAPL", "D"))
    }

    @Test
    fun `deleteOlderThan removes only stale rows`() = runTest {
        dao.insertAll(listOf(candle(timestamp = 1L, fetchedAt = 1L), candle(timestamp = 2L, fetchedAt = 100L)))
        dao.deleteOlderThan(thresholdMillis = 50L)
        assertEquals(listOf(2L), dao.getCandles("AAPL", "D").map { it.timestamp })
    }
}
