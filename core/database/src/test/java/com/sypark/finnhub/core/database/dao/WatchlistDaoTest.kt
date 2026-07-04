package com.sypark.finnhub.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.database.AppDatabase
import com.sypark.finnhub.core.database.entity.WatchlistEntity
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
class WatchlistDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: WatchlistDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.watchlistDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `insert then observeAll emits the inserted row`() = runTest {
        dao.insert(WatchlistEntity("AAPL", "Apple Inc.", AssetType.STOCK, sortOrder = 0, addedAt = 1L))

        dao.observeAll().test {
            assertEquals(listOf("AAPL"), awaitItem().map { it.symbol })
        }
    }

    @Test
    fun `delete removes the row`() = runTest {
        dao.insert(WatchlistEntity("AAPL", "Apple Inc.", AssetType.STOCK, sortOrder = 0, addedAt = 1L))
        dao.delete("AAPL")
        assertNull(dao.getBySymbol("AAPL"))
    }

    @Test
    fun `updateSortOrder changes only the targeted row's order`() = runTest {
        dao.insert(WatchlistEntity("AAPL", "Apple Inc.", AssetType.STOCK, sortOrder = 0, addedAt = 1L))
        dao.updateSortOrder("AAPL", sortOrder = 5)
        assertEquals(5, dao.getBySymbol("AAPL")?.sortOrder)
    }
}
