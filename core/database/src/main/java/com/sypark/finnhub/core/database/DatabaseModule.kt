package com.sypark.finnhub.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "finnhub.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            // Pre-release: schema changed 3 times during development (v1->v4) with no shipped
            // release and no production users yet, so destructive fallback (drop + recreate) remains
            // the pragmatic choice for that historical range -- no exported schema JSON exists for
            // v1-v3 to safely validate a hand-written migration against, and rewriting one blind
            // would be undetectable guesswork for versions nobody has installed.
            //
            // From v4 onward, AppDatabase.exportSchema = true and schema snapshots are committed
            // at core/database/schemas/... -- any FUTURE bump MUST add a real Migration tested with
            // androidx.room:room-testing's MigrationTestHelper (already a test dependency in this
            // module), not another destructive-fallback shortcut. v4 -> v5 (drops candle_cache) and
            // v5 -> v6 (adds earnings_cache) are real migrations; fallbackToDestructiveMigration
            // stays as the safety net for pre-v4 installs, which have no exported schema to migrate from.
            .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideWatchlistDao(database: AppDatabase): com.sypark.finnhub.core.database.dao.WatchlistDao =
        database.watchlistDao()

    @Provides
    @Singleton
    fun provideQuoteCacheDao(database: AppDatabase): com.sypark.finnhub.core.database.dao.QuoteCacheDao =
        database.quoteCacheDao()

    @Provides
    @Singleton
    fun providePriceAlertDao(database: AppDatabase): com.sypark.finnhub.core.database.dao.PriceAlertDao =
        database.priceAlertDao()

    @Provides
    @Singleton
    fun provideEarningsCacheDao(database: AppDatabase): com.sypark.finnhub.core.database.dao.EarningsCacheDao =
        database.earningsCacheDao()
}
