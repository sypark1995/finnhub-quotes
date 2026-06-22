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
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideWatchlistDao(database: AppDatabase): com.sypark.finnhub.core.database.dao.WatchlistDao =
        database.watchlistDao()

    @Provides
    @Singleton
    fun provideQuoteCacheDao(database: AppDatabase): com.sypark.finnhub.core.database.dao.QuoteCacheDao =
        database.quoteCacheDao()
}
