package com.example.sumit.data.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val syncPreference: Flow<Boolean>

    val langPreference: Flow<String>

    suspend fun setSyncPreference(pref: Boolean)

    suspend fun setLangPreference(pref: String)
}
