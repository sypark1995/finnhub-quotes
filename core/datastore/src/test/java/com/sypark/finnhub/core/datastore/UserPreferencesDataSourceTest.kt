package com.sypark.finnhub.core.datastore

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UserPreferencesDataSourceTest {

    private lateinit var dataSource: UserPreferencesDataSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dataSource = UserPreferencesDataSource(context)
    }

    @Test
    fun `refreshIntervalSeconds defaults to 30 when unset`() = runTest {
        dataSource.refreshIntervalSeconds.test {
            assertEquals(UserPreferencesDataSource.DEFAULT_REFRESH_INTERVAL_SECONDS, awaitItem())
        }
    }

    @Test
    fun `setRefreshIntervalSeconds updates the emitted value`() = runTest {
        dataSource.setRefreshIntervalSeconds(60)

        dataSource.refreshIntervalSeconds.test {
            assertEquals(60, awaitItem())
        }
    }

    @Test
    fun `themeMode defaults to SYSTEM when unset`() = runTest {
        dataSource.themeMode.test {
            assertEquals(StoredThemeMode.SYSTEM, awaitItem())
        }
    }

    @Test
    fun `setThemeMode updates the emitted value`() = runTest {
        dataSource.setThemeMode(StoredThemeMode.DARK)
        dataSource.themeMode.test {
            assertEquals(StoredThemeMode.DARK, awaitItem())
        }
    }
}
