package com.sypark.finnhub.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.sypark.finnhub.core.database.AppDatabase
import com.sypark.finnhub.core.database.entity.QuoteCacheEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class QuoteCacheDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: QuoteCacheDao

    private fun sample(symbol: String = "AAPL", updatedAt: Long = 1L) = QuoteCacheEntity(
        symbol = symbol, price = 198.5, change = 2.3, changePercent = 1.17,
        high = 199.1, low = 196.8, open = 197.2, previousClose = 196.2, updatedAt = updatedAt,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.quoteCacheDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `upsert then observe emits the cached quote`() = runTest {
        dao.upsert(sample())
        dao.observe("AAPL").test {
            assertEquals(198.5, awaitItem()?.price)
        }
    }

    @Test
    fun `upsert with the same symbol replaces the previous row`() = runTest {
        dao.upsert(sample(updatedAt = 1L))
        dao.upsert(sample(updatedAt = 2L))
        dao.observeAll().test {
            assertEquals(1, awaitItem().size)
        }
    }
}
