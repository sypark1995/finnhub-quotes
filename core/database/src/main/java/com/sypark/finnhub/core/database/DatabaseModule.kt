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
            // Pre-release: schema has changed 3 times during development (v1->v4) with no shipped
            // release and no production users yet, so a destructive fallback (drop + recreate) is
            // the pragmatic choice over hand-written migrations for a schema still in flux.
            // MUST be replaced with real Migration objects before this app's first real release,
            // since WatchlistEntity/PriceAlertEntity hold real user data at that point.
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
