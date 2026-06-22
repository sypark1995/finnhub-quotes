package com.sypark.finnhub.core.websocket

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
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
        fun provideWebSocketJson(): Json = Json { ignoreUnknownKeys = true }

        @Provides
        @Singleton
        fun provideWebSocketOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .pingInterval(20, TimeUnit.SECONDS)
            .build()
    }
}
