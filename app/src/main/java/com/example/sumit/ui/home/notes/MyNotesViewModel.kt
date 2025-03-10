package com.example.sumit.ui.home.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.notes.Note
import com.example.sumit.data.notes.NotesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MyNotesViewModel(notesRepository: NotesRepository) : ViewModel() {
    val myNotesUiState: StateFlow<MyNotesUiState> = notesRepository
        .getAllNotesStream()
        .map { MyNotesUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = MyNotesUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class MyNotesUiState(val myNotes: List<Note> = listOf())
