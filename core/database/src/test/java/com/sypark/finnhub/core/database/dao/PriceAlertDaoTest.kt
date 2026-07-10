package com.sypark.finnhub.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.database.AppDatabase
import com.sypark.finnhub.core.database.entity.PriceAlertEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PriceAlertDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: PriceAlertDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.priceAlertDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `insert then observeAll emits the inserted alert`() = runTest {
        dao.insert(PriceAlertEntity(symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE, isEnabled = true, createdAt = 1L))
        dao.observeAll().test {
            assertEquals(1, awaitItem().size)
        }
    }

    @Test
    fun `observeEnabled excludes disabled and already-triggered alerts`() = runTest {
        dao.insert(PriceAlertEntity(symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE, isEnabled = true, createdAt = 1L))
        dao.insert(PriceAlertEntity(symbol = "MSFT", targetPrice = 400.0, condition = AlertCondition.BELOW, isEnabled = false, createdAt = 1L))
        dao.insert(PriceAlertEntity(symbol = "TSLA", targetPrice = 250.0, condition = AlertCondition.ABOVE, isEnabled = true, triggeredAt = 5L, createdAt = 1L))

        dao.observeEnabled().test {
            assertEquals(listOf("AAPL"), awaitItem().map { it.symbol })
        }
    }

    @Test
    fun `update changes targetPrice, condition, and isEnabled but preserves createdAt`() = runTest {
        val id = dao.insert(PriceAlertEntity(symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE, isEnabled = true, createdAt = 1L))
        dao.update(id, targetPrice = 220.0, condition = AlertCondition.BELOW, isEnabled = false)
        dao.observeAll().test {
            val updated = awaitItem().single()
            assertEquals(220.0, updated.targetPrice)
            assertEquals(AlertCondition.BELOW, updated.condition)
            assertEquals(false, updated.isEnabled)
            assertEquals(1L, updated.createdAt)
        }
    }

    @Test
    fun `markTriggered sets triggeredAt`() = runTest {
        val id = dao.insert(PriceAlertEntity(symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE, isEnabled = true, createdAt = 1L))
        dao.markTriggered(id, triggeredAt = 99L)
        dao.observeAll().test {
            assertNotNull(awaitItem().first().triggeredAt)
        }
    }

    @Test
    fun `delete removes the row`() = runTest {
        val id = dao.insert(PriceAlertEntity(symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE, isEnabled = true, createdAt = 1L))
        dao.delete(id)
        dao.observeAll().test {
            assertEquals(0, awaitItem().size)
        }
    }
}
