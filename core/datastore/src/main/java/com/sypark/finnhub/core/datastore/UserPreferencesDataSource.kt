package com.sypark.finnhub.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val REFRESH_INTERVAL_SECONDS = intPreferencesKey("refresh_interval_seconds")
    }

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("user_preferences") }
    )

    val refreshIntervalSeconds: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.REFRESH_INTERVAL_SECONDS] ?: DEFAULT_REFRESH_INTERVAL_SECONDS
    }

    suspend fun setRefreshIntervalSeconds(seconds: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.REFRESH_INTERVAL_SECONDS] = seconds
        }
    }

    companion object {
        const val DEFAULT_REFRESH_INTERVAL_SECONDS = 30
    }
}
