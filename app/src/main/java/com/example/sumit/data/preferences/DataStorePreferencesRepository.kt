package com.example.sumit.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
    override val syncPreference: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[SYNC_ENABLED] ?: false
        }

    override val langPreference: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[LANG_CODE] ?: "HU"
        }

    override suspend fun setSyncPreference(pref: Boolean) {
        dataStore.edit { preferences ->
            preferences[SYNC_ENABLED] = pref
        }
    }

    override suspend fun setLangPreference(pref: String) {
        dataStore.edit { preferences ->
            preferences[LANG_CODE] = pref
        }
    }

    private companion object {
        val SYNC_ENABLED = booleanPreferencesKey("sync_enabled")
        val LANG_CODE = stringPreferencesKey("lang_code")
    }
}
