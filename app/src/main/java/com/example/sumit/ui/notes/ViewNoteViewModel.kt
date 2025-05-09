package com.example.sumit.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.notes.Note
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.users.FriendData
import com.example.sumit.data.users.FriendshipStatus
import com.example.sumit.data.users.UserRepository
import com.example.sumit.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class ViewNoteViewModel(
    notesRepository: NotesRepository,
    userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val viewedNoteUiState: StateFlow<Note> = notesRepository
        .getNoteStream(checkNotNull(savedStateHandle[ViewNoteDestination.noteIdArg]))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = defaultNote
        )

    val friendList: StateFlow<List<FriendData>> = userRepository.currentUser
        .flatMapLatest { user ->
            if (user?.uid != null) {
                userRepository.getUserFriends(user.uid)
            } else {
                flowOf(emptyList())
            }
        }
        .map { friendData ->
            friendData.filter { it.friendshipData.status == FriendshipStatus.Accepted.toString() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    fun shareWithFriends(userIds: List<String>) {

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