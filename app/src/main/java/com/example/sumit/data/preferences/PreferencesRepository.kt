package com.example.sumit.data.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Handles locally stored user preferences.
 */
interface PreferencesRepository {
    /**
     * Whether periodic synchronization of notes should occur.
     */
    val syncPreference: Flow<Boolean>

    /**
     * The language to use to display text in the application.
     */
    val langPreference: Flow<String>

    /**
     * Sets the preference for synchronizing notes.
     * @param pref Whether synchronization should be enabled
     */
    suspend fun setSyncPreference(pref: Boolean)

    /**
     * Sets the preference for the display language.
     * @param pref The code of the language to use
     */
    suspend fun setLangPreference(pref: String)
}
