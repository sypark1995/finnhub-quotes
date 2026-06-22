package com.sypark.finnhub.core.websocket

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WebSocketModule {

    @Binds
    @Singleton
    abstract fun bindFinnhubWebSocketManager(impl: FinnhubWebSocketManagerImpl): FinnhubWebSocketManager

    companion object {
        @Provides
        @Singleton
        fun provideWebSocketUrl(): String = "wss://ws.finnhub.io?token=${BuildConfig.FINNHUB_API_KEY}"
    }
}
