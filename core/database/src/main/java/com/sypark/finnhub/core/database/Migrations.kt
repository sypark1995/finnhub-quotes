package com.sypark.finnhub.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Drops candle_cache: the chart feature that used it was removed. */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS candle_cache")
    }
}

/** Adds earnings_cache for the earnings calendar screen's local cache. */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `earnings_cache` (
                `symbol` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `hour` TEXT NOT NULL,
                `epsEstimate` REAL,
                `epsActual` REAL,
                `revenueEstimate` REAL,
                `revenueActual` REAL,
                `fetchedAt` INTEGER NOT NULL,
                PRIMARY KEY(`symbol`, `date`)
            )
            """.trimIndent(),
        )
    }
}
