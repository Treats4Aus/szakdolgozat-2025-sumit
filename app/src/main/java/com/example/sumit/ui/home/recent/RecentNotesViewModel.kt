package com.example.sumit.ui.home.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.notes.RemoteNotesRepository
import com.example.sumit.data.users.UserRepository
import com.example.sumit.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class RecentNotesViewModel(
    notesRepository: NotesRepository,
    remoteNotesRepository: RemoteNotesRepository,
    userRepository: UserRepository
) : ViewModel() {
    val recentNotes = notesRepository
        .getRecentNotesStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val sharedNotes = userRepository.currentUser
        .flatMapLatest { user ->
            if (user?.uid != null) {
                remoteNotesRepository.getUserSharedNotes(user.uid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )
}
