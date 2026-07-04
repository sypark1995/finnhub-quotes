package com.sypark.finnhub.core.data.repository

import com.sypark.finnhub.core.common.AppDispatchers
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.database.dao.WatchlistDao
import com.sypark.finnhub.core.database.entity.WatchlistEntity
import com.sypark.finnhub.core.domain.model.WatchlistItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WatchlistRepositoryImplTest {

    private val dao = mockk<WatchlistDao>(relaxUnitFun = true)
    private val repository = WatchlistRepositoryImpl(dao, AppDispatchers())

    @Test
    fun `observeWatchlist maps every entity to a domain WatchlistItem`() = runTest {
        every { dao.observeAll() } returns flowOf(listOf(WatchlistEntity("AAPL", "Apple Inc.", AssetType.STOCK, 0, 1L)))

        val result = repository.observeWatchlist()

        result.collect { items -> assertEquals(listOf(WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0)), items) }
    }

    @Test
    fun `add inserts the mapped entity and returns Success`() = runTest {
        val result = repository.add(WatchlistItem("AAPL", "Apple Inc.", AssetType.STOCK, 0))

        assertTrue(result is AppResult.Success)
        coVerify { dao.insert(match { it.symbol == "AAPL" }) }
    }

    @Test
    fun `remove deletes by symbol and returns Success`() = runTest {
        val result = repository.remove("AAPL")
        assertTrue(result is AppResult.Success)
        coVerify { dao.delete("AAPL") }
    }

    @Test
    fun `isInWatchlist returns true only when the DAO has a row for that symbol`() = runTest {
        coEvery { dao.getBySymbol("AAPL") } returns WatchlistEntity("AAPL", "Apple Inc.", AssetType.STOCK, 0, 1L)
        coEvery { dao.getBySymbol("MSFT") } returns null

        assertTrue(repository.isInWatchlist("AAPL"))
        assertTrue(!repository.isInWatchlist("MSFT"))
    }
}
