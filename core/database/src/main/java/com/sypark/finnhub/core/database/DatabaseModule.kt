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
            // From v4 onward, AppDatabase.exportSchema = true and a v4 schema snapshot is committed
            // at core/database/schemas/.../4.json -- any FUTURE bump (v4 -> v5+, e.g. before a real
            // release ships) MUST add a real Migration(4, 5) tested with
            // androidx.room:room-testing's MigrationTestHelper (already a test dependency in this
            // module), not another destructive-fallback shortcut.
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
    fun provideCandleCacheDao(database: AppDatabase): com.sypark.finnhub.core.database.dao.CandleCacheDao =
        database.candleCacheDao()

    @Provides
    @Singleton
    fun providePriceAlertDao(database: AppDatabase): com.sypark.finnhub.core.database.dao.PriceAlertDao =
        database.priceAlertDao()
}
