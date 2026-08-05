package com.sypark.finnhub.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sypark.finnhub.core.database.AppDatabase
import com.sypark.finnhub.core.database.entity.EarningsCacheEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class EarningsCacheDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: EarningsCacheDao

    private fun sample(symbol: String = "AAPL", date: String = "2026-10-28", fetchedAt: Long = 1L) = EarningsCacheEntity(
        symbol = symbol, date = date, hour = "amc",
        epsEstimate = 2.05, epsActual = null, revenueEstimate = null, revenueActual = null,
        fetchedAt = fetchedAt,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.earningsCacheDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `insertAll then getForSymbol returns every row for that symbol ordered by date`() = runTest {
        dao.insertAll(listOf(sample(date = "2027-01-27"), sample(date = "2026-10-28")))

        val result = dao.getForSymbol("AAPL")

        assertEquals(listOf("2026-10-28", "2027-01-27"), result.map { it.date })
    }

    @Test
    fun `getForSymbol only returns rows for the requested symbol`() = runTest {
        dao.insertAll(listOf(sample(symbol = "AAPL"), sample(symbol = "MSFT")))

        assertEquals(listOf("AAPL"), dao.getForSymbol("AAPL").map { it.symbol })
    }

    @Test
    fun `getLatestFetchedAt returns null when nothing is cached for the symbol`() = runTest {
        assertNull(dao.getLatestFetchedAt("AAPL"))
    }

    @Test
    fun `replaceForSymbol clears the previous rows for that symbol before inserting the new ones`() = runTest {
        dao.insertAll(listOf(sample(date = "2026-07-30", fetchedAt = 1L)))

        dao.replaceForSymbol("AAPL", listOf(sample(date = "2026-10-28", fetchedAt = 2L)))

        val result = dao.getForSymbol("AAPL")
        assertEquals(1, result.size)
        assertEquals("2026-10-28", result.single().date)
    }

    @Test
    fun `replaceForSymbol does not touch other symbols' rows`() = runTest {
        dao.insertAll(listOf(sample(symbol = "MSFT", date = "2026-07-22")))

        dao.replaceForSymbol("AAPL", listOf(sample(symbol = "AAPL", date = "2026-10-28")))

        assertTrue(dao.getForSymbol("MSFT").isNotEmpty())
    }
}
