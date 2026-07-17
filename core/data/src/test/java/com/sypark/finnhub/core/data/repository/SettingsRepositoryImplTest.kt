package com.sypark.finnhub.core.data.repository

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.datastore.StoredThemeMode
import com.sypark.finnhub.core.datastore.UserPreferencesDataSource
import com.sypark.finnhub.core.domain.model.ThemeMode
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SettingsRepositoryImplTest {

    private val dataSource = mockk<UserPreferencesDataSource>(relaxUnitFun = true)
    private val repository = SettingsRepositoryImpl(dataSource)

    @Test
    fun `observeThemeMode maps the data source's StoredThemeMode to the domain ThemeMode`() = runTest {
        every { dataSource.themeMode } returns flowOf(StoredThemeMode.DARK)
        assertEquals(ThemeMode.DARK, repository.observeThemeMode().first())
    }

    @Test
    fun `setThemeMode maps to StoredThemeMode and returns Success`() = runTest {
        val result = repository.setThemeMode(ThemeMode.LIGHT)
        assertTrue(result is AppResult.Success)
        coVerify { dataSource.setThemeMode(StoredThemeMode.LIGHT) }
    }
}
