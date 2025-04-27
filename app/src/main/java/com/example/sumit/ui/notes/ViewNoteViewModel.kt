package com.example.sumit.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.notes.Note
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Date

class ViewNoteViewModel(
    notesRepository: NotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val viewedNoteUiState: StateFlow<Note> = notesRepository
        .getNoteStream(checkNotNull(savedStateHandle[ViewNoteDestination.noteIdArg]))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = defaultNote
        )

    companion object {
        private val defaultNote = Note(
            created = Date(),
            lastModified = Date(),
            title = "",
            content = "",
            summary = ""
        )
    }
}