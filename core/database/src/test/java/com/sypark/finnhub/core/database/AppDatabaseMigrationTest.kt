package com.sypark.finnhub.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppDatabaseMigrationTest {

    private val testDbName = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName!!,
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun `migrate 4 to 5 drops candle_cache and preserves the other tables' data`() {
        helper.createDatabase(testDbName, 4).apply {
            execSQL(
                "INSERT INTO watchlist (symbol, displayName, assetType, sortOrder, addedAt) VALUES ('AAPL', 'Apple Inc.', 'STOCK', 0, 1)",
            )
            execSQL(
                "INSERT INTO candle_cache (symbol, resolution, timestamp, open, high, low, close, volume, fetchedAt) " +
                    "VALUES ('AAPL', 'D', 1, 197.2, 199.1, 196.8, 198.5, 1000, 1)",
            )
            close()
        }

        val migrated = helper.runMigrationsAndValidate(testDbName, 5, true, MIGRATION_4_5)

        val tableNames = mutableListOf<String>()
        migrated.query("SELECT name FROM sqlite_master WHERE type='table'").use { cursor ->
            while (cursor.moveToNext()) tableNames.add(cursor.getString(0))
        }
        org.junit.Assert.assertFalse("candle_cache", "candle_cache" in tableNames)

        migrated.query("SELECT symbol FROM watchlist").use { cursor ->
            org.junit.Assert.assertTrue(cursor.moveToFirst())
            org.junit.Assert.assertEquals("AAPL", cursor.getString(0))
        }
    }
}
