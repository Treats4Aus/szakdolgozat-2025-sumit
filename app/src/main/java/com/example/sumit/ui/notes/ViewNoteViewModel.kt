package com.example.sumit.ui.notes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.R
import com.example.sumit.data.notes.Note
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.notes.RemoteNotesRepository
import com.example.sumit.data.translations.TranslationsRepository
import com.example.sumit.data.users.FriendData
import com.example.sumit.data.users.FriendshipStatus
import com.example.sumit.data.users.UserRepository
import com.example.sumit.utils.TIMEOUT_MILLIS
import com.google.firebase.FirebaseException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

private const val TAG = "ViewNoteViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class ViewNoteViewModel(
    notesRepository: NotesRepository,
    private val remoteNotesRepository: RemoteNotesRepository,
    private val userRepository: UserRepository,
    private val translationsRepository: TranslationsRepository,
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

    private val _shareUiState = MutableStateFlow(ShareUiState())
    val shareUiState = _shareUiState.asStateFlow()

    fun shareWithFriends(userIds: List<String>) {
        if (userIds.isEmpty()) {
            setMessage(translationsRepository.getTranslation(R.string.you_must_select_at_least_one_person))
            return
        }

        setLoading(true)
        viewModelScope.launch {
            val user = userRepository.currentUser.first()
            if (user == null) {
                setMessage(translationsRepository.getTranslation(R.string.you_must_sign_in_to_share))
                setLoading(false)
                return@launch
            }

            val remoteNotes = remoteNotesRepository.getUserNotes(user.uid).first()
            Log.d(TAG, "Local note firebaseId: ${viewedNoteUiState.value.firebaseId}")
            val remoteVersion = remoteNotes.find { it.id == viewedNoteUiState.value.firebaseId }
            if (remoteVersion == null) {
                setMessage(translationsRepository.getTranslation(R.string.you_must_enable_sync_to_share))
                setLoading(false)
                return@launch
            }

            val updatedNote = remoteVersion.copy(
                sharedWith = remoteVersion.sharedWith + userIds
            )
            try {
                remoteNotesRepository.updateNote(updatedNote)
                setMessage(translationsRepository.getTranslation(R.string.note_successfully_shared))
            } catch (e: FirebaseException) {
                setMessage(translationsRepository.getTranslation(R.string.note_sharing_failed))
            }
            setLoading(false)
        }
    }

    fun resetMessage() {
        _shareUiState.update { currentState ->
            currentState.copy(
                message = ""
            )
        }
    }

    private fun setMessage(message: String) {
        _shareUiState.update { currentState ->
            currentState.copy(
                message = message
            )
        }
    }

    private fun setLoading(value: Boolean) {
        _shareUiState.update { currentState ->
            currentState.copy(
                loading = value
            )
        }
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

data class ShareUiState(
    val message: String = "",
    val loading: Boolean = false
)
