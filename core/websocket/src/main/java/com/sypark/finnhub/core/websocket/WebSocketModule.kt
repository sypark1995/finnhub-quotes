package com.sypark.finnhub.core.websocket

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebSocketOkHttpClient

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

        @Provides
        @Singleton
        @WebSocketOkHttpClient
        fun provideWebSocketOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .pingInterval(20, TimeUnit.SECONDS)
            .build()
    }
}
