package com.example.sumit.ui.home.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.notes.Note
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.notes.RemoteNotesRepository
import com.example.sumit.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyNotesViewModel(
    private val notesRepository: NotesRepository,
    private val remoteNotesRepository: RemoteNotesRepository
) : ViewModel() {
    val myNotesUiState: StateFlow<MyNotesUiState> = notesRepository
        .getAllNotesStream()
        .map { MyNotesUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = MyNotesUiState()
        )

    fun deleteNote(note: Note) = viewModelScope.launch {
        notesRepository.deleteNote(note)

        if (note.firebaseId != null) {
            remoteNotesRepository.deleteNote(note.firebaseId)
        }
    }
}

data class MyNotesUiState(val myNotes: List<Note> = listOf())
