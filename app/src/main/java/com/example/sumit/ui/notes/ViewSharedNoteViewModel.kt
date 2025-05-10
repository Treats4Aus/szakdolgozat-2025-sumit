package com.example.sumit.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.notes.RemoteNote
import com.example.sumit.data.notes.RemoteNotesRepository
import com.example.sumit.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ViewSharedNoteViewModel(
    remoteNotesRepository: RemoteNotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val viewedNoteUiState: StateFlow<RemoteNote?> = remoteNotesRepository
        .getNote(checkNotNull(savedStateHandle[ViewSharedNoteDestination.noteIdArg]))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = null
        )
}
