package com.sypark.finnhub.core.data.di

import com.sypark.finnhub.core.data.repository.MarketRepositoryImpl
import com.sypark.finnhub.core.data.repository.WatchlistRepositoryImpl
import com.sypark.finnhub.core.domain.repository.MarketRepository
import com.sypark.finnhub.core.domain.repository.WatchlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository

    @Binds
    @Singleton
    abstract fun bindMarketRepository(impl: MarketRepositoryImpl): MarketRepository
}
