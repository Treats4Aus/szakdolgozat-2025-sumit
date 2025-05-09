package com.example.sumit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.notes.RemoteNotesRepository
import com.example.sumit.data.preferences.PreferencesRepository
import com.example.sumit.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val remoteNotesRepository: RemoteNotesRepository,
) : ViewModel() {
    val syncEnabled = preferencesRepository.syncPreference
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = false
        )

    val langCode = preferencesRepository.langPreference
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = "HU"
        )

    fun updateSyncPreference(pref: Boolean) = viewModelScope.launch {
        if (pref) {
            remoteNotesRepository.startSync()
        } else {
            remoteNotesRepository.cancelSync()
        }

        preferencesRepository.setSyncPreference(pref)
    }

    fun updateLangPreference(pref: String) = viewModelScope.launch {
        preferencesRepository.setLangPreference(pref)
    }
}
