package com.sypark.finnhub.core.data.repository

import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AppDispatchers
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.database.dao.PriceAlertDao
import com.sypark.finnhub.core.database.entity.PriceAlertEntity
import com.sypark.finnhub.core.domain.model.PriceAlert
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AlertRepositoryImplTest {

    private val dao = mockk<PriceAlertDao>(relaxUnitFun = true)
    private val repository = AlertRepositoryImpl(dao, AppDispatchers())

    @Test
    fun `observeAlerts maps every entity to a domain PriceAlert`() = runTest {
        every { dao.observeAll() } returns flowOf(listOf(PriceAlertEntity(1, "AAPL", 210.0, AlertCondition.ABOVE, true, null, 1L)))
        repository.observeAlerts().collect { alerts -> assertEquals(listOf(PriceAlert(1, "AAPL", 210.0, AlertCondition.ABOVE, true, null)), alerts) }
    }

    @Test
    fun `create inserts the mapped entity and returns its generated id`() = runTest {
        coEvery { dao.insert(any()) } returns 42L
        val result = repository.create(PriceAlert(0, "AAPL", 210.0, AlertCondition.ABOVE, true, null))
        assertEquals(AppResult.Success(42L), result)
    }

    @Test
    fun `update delegates to the DAO's targeted column update, leaving createdAt untouched`() = runTest {
        val result = repository.update(PriceAlert(1, "AAPL", 220.0, AlertCondition.BELOW, false, null))
        assertTrue(result is AppResult.Success)
        coVerify { dao.update(1L, 220.0, AlertCondition.BELOW, false) }
    }

    @Test
    fun `markTriggered delegates to the DAO with the current time`() = runTest {
        repository.markTriggered(1L)
        coVerify { dao.markTriggered(1L, any()) }
    }

    @Test
    fun `delete removes by id and returns Success`() = runTest {
        val result = repository.delete(1L)
        assertTrue(result is AppResult.Success)
        coVerify { dao.delete(1L) }
    }
}
