package com.example.sumit.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.R
import com.example.sumit.data.notes.NotesRepository
import com.example.sumit.data.notes.RemoteNotesRepository
import com.example.sumit.data.preferences.PreferencesRepository
import com.example.sumit.data.translations.TranslationsRepository
import com.example.sumit.data.users.FriendData
import com.example.sumit.data.users.FriendshipStatus
import com.example.sumit.data.users.UserRepository
import com.example.sumit.utils.PasswordValidator
import com.example.sumit.utils.TIMEOUT_MILLIS
import com.example.sumit.utils.TranslationPasswordValidator
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val notesRepository: NotesRepository,
    private val remoteNotesRepository: RemoteNotesRepository,
    private val preferencesRepository: PreferencesRepository,
    private val translationsRepository: TranslationsRepository
) : ViewModel() {
    private val passwordValidator: PasswordValidator =
        TranslationPasswordValidator(translationsRepository)

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    val currentUser = userRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = null
        )

    val userData = currentUser
        .flatMapLatest { user ->
            if (user?.uid != null) {
                userRepository.getUserData(user.uid)
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = null
        )

    val friendList = currentUser
        .flatMapLatest { user ->
            if (user?.uid != null) {
                userRepository.getUserFriends(user.uid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    private val _passwordChangeUiState = MutableStateFlow(PasswordChangeUiState())
    val passwordChangeUiState = _passwordChangeUiState.asStateFlow()

    private val _currentMessageRes = MutableStateFlow("")
    val currentMessageRes = _currentMessageRes.asStateFlow()

    fun updateEmail(email: String) =
        updateLoginForm(_loginUiState.value.form.copy(email = email))

    fun updatePassword(password: String) =
        updateLoginForm(_loginUiState.value.form.copy(password = password))

    fun resetLoginForm() {
        _loginUiState.update { currentState ->
            currentState.copy(
                form = LoginFormData()
            )
        }
    }

    fun signInWithEmailAndPassword() {
        val form = _loginUiState.value.form

        Log.d(TAG, "Email: ${form.email} Password: ${form.password}")

        if (form.email.isEmpty() || form.password.isEmpty()) {
            return
        }

        viewModelScope.launch {
            setLoginInProgress(true)

            try {
                userRepository.signInWithEmailAndPassword(form.email, form.password)

                resetLoginForm()
            } catch (e: FirebaseException) {
                Log.e(TAG, "Login error", e)
                setMessage(translationsRepository.getTranslation(R.string.incorrect_email_or_password))
            }

            if (preferencesRepository.syncPreference.first()) {
                remoteNotesRepository.startSync()
            }

            setLoginInProgress(false)
        }
    }

    fun updateCurrentPassword(password: String) =
        updatePasswordChangeForm(_passwordChangeUiState.value.form.copy(currentPassword = password))

    fun updateNewPassword(password: String) =
        updatePasswordChangeForm(_passwordChangeUiState.value.form.copy(newPassword = password))

    fun updateNewPasswordConfirm(password: String) =
        updatePasswordChangeForm(_passwordChangeUiState.value.form.copy(newPasswordConfirm = password))

    private fun resetPasswordChangeForm() {
        _passwordChangeUiState.update { currentState ->
            currentState.copy(
                form = PasswordChangeFormData()
            )
        }
    }

    fun togglePasswordChangeFormVisibility() {
        _passwordChangeUiState.update { currentState ->
            currentState.copy(
                passwordChangeVisible = !currentState.passwordChangeVisible
            )
        }
    }

    fun changePassword() {
        val form = _passwordChangeUiState.value.form

        Log.d(TAG, "Old password: ${form.currentPassword} New password: ${form.newPassword}")

        if (form.currentPassword.isEmpty() || form.newPassword.isEmpty()) {
            setMessage(translationsRepository.getTranslation(R.string.please_fill_out_every_field))
            return
        }

        val validationResult = passwordValidator.validate(form.newPassword)
        if (validationResult != null) {
            setMessage(validationResult)
            return
        }

        if (form.newPassword != form.newPasswordConfirm) {
            setMessage(translationsRepository.getTranslation(R.string.password_and_confirm_must_match))
            return
        }

        val userEmail = userData.value?.email
        if (userEmail == null) {
            setMessage(translationsRepository.getTranslation(R.string.must_sign_in_first))
            return
        }

        viewModelScope.launch {
            setPasswordChangeInProgress(true)

            try {
                userRepository.changePassword(userEmail, form.currentPassword, form.newPassword)

                _passwordChangeUiState.update { currentState ->
                    currentState.copy(
                        passwordChangeVisible = false
                    )
                }

                setMessage(translationsRepository.getTranslation(R.string.password_changed_successfully))

                resetPasswordChangeForm()
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.e(TAG, "Change password invalid credentials")
                setMessage(translationsRepository.getTranslation(R.string.incorrect_password))
            } catch (e: FirebaseException) {
                Log.e(TAG, "Change password error", e)
                setMessage(translationsRepository.getTranslation(R.string.password_change_failed))
            }

            setPasswordChangeInProgress(false)
        }
    }

    fun signOut() {
        userRepository.signOut()
        resetPasswordChangeForm()

        viewModelScope.launch {
            notesRepository.clearNotes()
        }
    }

    fun sendFriendRequest(email: String) {
        currentUser.value?.let {
            viewModelScope.launch {
                if (!userRepository.validateEmail(email)) {
                    setMessage(translationsRepository.getTranslation(R.string.no_user_with_email))
                    return@launch
                }

                when (userRepository.checkFriendshipStatus(it.uid, email)) {
                    FriendshipStatus.Accepted ->
                        setMessage(translationsRepository.getTranslation(R.string.user_already_friend))

                    FriendshipStatus.Blocked ->
                        setMessage(translationsRepository.getTranslation(R.string.user_blocked_you))

                    FriendshipStatus.Pending ->
                        setMessage(translationsRepository.getTranslation(R.string.friend_request_already_pending))

                    else -> try {
                        userRepository.sendFriendRequest(it.uid, email)
                        setMessage(translationsRepository.getTranslation(R.string.friend_request_sent))
                    } catch (e: Exception) {
                        if (e is CancellationException) {
                            throw e
                        }
                        setMessage(translationsRepository.getTranslation(R.string.friend_request_failed))
                    }
                }
            }
        }
    }

    fun acceptFriendRequest(friendData: FriendData) = viewModelScope.launch {
        userRepository.acceptFriendRequest(friendData.friendshipData)
        setMessage(translationsRepository.getTranslation(R.string.friend_request_accepted))
    }

    fun rejectFriendRequest(friendData: FriendData) = viewModelScope.launch {
        userRepository.rejectFriendRequest(friendData.friendshipData)
        setMessage(translationsRepository.getTranslation(R.string.friend_request_rejected))
    }

    fun removeFriend(friendData: FriendData) = viewModelScope.launch {
        userRepository.rejectFriendRequest(friendData.friendshipData)
        setMessage(translationsRepository.getTranslation(R.string.friend_removed))
    }

    fun blockFriend(friendData: FriendData) = viewModelScope.launch {
        userRepository.blockFriend(friendData.friendshipData)
        setMessage(translationsRepository.getTranslation(R.string.user_blocked))
    }

    fun resetMessage() {
        _currentMessageRes.value = ""
    }

    private fun setMessage(message: String) {
        _currentMessageRes.value = message
    }

    private fun updateLoginForm(formData: LoginFormData) {
        _loginUiState.update { currentState ->
            currentState.copy(
                form = formData
            )
        }
    }

    private fun setLoginInProgress(value: Boolean) {
        _loginUiState.update { currentState ->
            currentState.copy(
                loginInProgress = value
            )
        }
    }

    private fun updatePasswordChangeForm(formData: PasswordChangeFormData) {
        _passwordChangeUiState.update { currentState ->
            currentState.copy(
                form = formData
            )
        }
    }

    private fun setPasswordChangeInProgress(value: Boolean) {
        _passwordChangeUiState.update { currentState ->
            currentState.copy(
                passwordChangeInProgress = value
            )
        }
    }
}

data class LoginUiState(
    val loginInProgress: Boolean = false,
    val form: LoginFormData = LoginFormData()
)

data class LoginFormData(
    val email: String = "",
    val password: String = ""
)

data class PasswordChangeUiState(
    val form: PasswordChangeFormData = PasswordChangeFormData(),
    val passwordChangeVisible: Boolean = false,
    val passwordChangeInProgress: Boolean = false
)

data class PasswordChangeFormData(
    val currentPassword: String = "",
    val newPassword: String = "",
    val newPasswordConfirm: String = ""
)
