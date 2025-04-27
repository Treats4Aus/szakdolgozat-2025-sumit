package com.example.sumit.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.notes.Note
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Date

class EditNoteViewModel(
    private val notesRepository: NotesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _viewedNote = MutableStateFlow(defaultNote)
    val viewedNote = _viewedNote
        .onStart {
            _viewedNote.value = notesRepository
                .getNoteStream(checkNotNull(savedStateHandle[EditNoteDestination.noteIdArg]))
                .first()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = defaultNote
        )

    fun editTitle(newTitle: String) {
        _viewedNote.update { currentState ->
            currentState.copy(
                title = newTitle
            )
        }
    }

    fun editContent(newContent: String) {
        _viewedNote.update { currentState ->
            currentState.copy(
                content = newContent
            )
        }
    }

    suspend fun saveNote() {
        notesRepository.updateNote(
            _viewedNote.value.copy(
                lastModified = Date()
            )
        )
    }

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
