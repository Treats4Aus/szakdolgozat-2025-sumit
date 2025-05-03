package com.example.sumit.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.users.UserRepository
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    private val _profileUiState = MutableStateFlow(
        ProfileUiState(
            loggedIn = userRepository.currentUser != null,
            email = userRepository.currentUser?.email ?: "",
            name = userRepository.currentUser?.displayName ?: "",
            username = "User"
        )
    )
    val profileUiState = _profileUiState.asStateFlow()

    private val _currentErrorMessage = MutableStateFlow("")
    val currentErrorMessage = _currentErrorMessage.asStateFlow()

    fun updateEmail(email: String) =
        updateLoginForm(_loginUiState.value.form.copy(email = email))

    fun updatePassword(password: String) =
        updateLoginForm(_loginUiState.value.form.copy(password = password))

    fun signInWithEmailAndPassword() {
        Log.d(
            TAG,
            "Email: ${_loginUiState.value.form.email} " +
                    "Password: ${_loginUiState.value.form.password}"
        )

        viewModelScope.launch {
            setLoginInProgress(true)

            try {
                userRepository.signInWithEmailAndPassword(
                    _loginUiState.value.form.email,
                    _loginUiState.value.form.password
                )

                _profileUiState.update { currentState ->
                    currentState.copy(
                        loggedIn = true,
                        email = userRepository.currentUser?.email ?: "",
                        name = userRepository.currentUser?.displayName ?: "",
                        username = "User"
                    )
                }
            } catch (e: FirebaseException) {
                Log.e(TAG, "Login error", e)
                setErrorMessage("Incorrect email address or password")
            }

            setLoginInProgress(false)
        }
    }

    fun updateCurrentPassword(password: String) =
        updatePasswordChangeForm(_profileUiState.value.form.copy(currentPassword = password))

    fun updateNewPassword(password: String) =
        updatePasswordChangeForm(_profileUiState.value.form.copy(newPassword = password))

    fun updateNewPasswordConfirm(password: String) =
        updatePasswordChangeForm(_profileUiState.value.form.copy(newPasswordConfirm = password))

    fun changePassword() {
        Log.d(
            TAG,
            "Old password: ${_profileUiState.value.form.currentPassword} " +
                    "New password: ${_profileUiState.value.form.newPassword}"
        )

        viewModelScope.launch {
            setPasswordChangeInProgress(true)

            userRepository.changePassword(
                _profileUiState.value.email,
                _profileUiState.value.form.currentPassword,
                _profileUiState.value.form.newPassword
            )

            setPasswordChangeInProgress(false)
        }
    }

    fun signOut() {
        userRepository.signOut()

        _profileUiState.update { currentState ->
            currentState.copy(
                loggedIn = false
            )
        }
    }

    fun resetErrorMessage() {
        _currentErrorMessage.value = ""
    }

    private fun setErrorMessage(message: String) {
        _currentErrorMessage.value = message
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
        _profileUiState.update { currentState ->
            currentState.copy(
                form = formData
            )
        }
    }

    private fun setPasswordChangeInProgress(value: Boolean) {
        _profileUiState.update { currentState ->
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

data class ProfileUiState(
    val loggedIn: Boolean = false,
    val email: String = "",
    val name: String = "",
    val username: String = "",
    val form: PasswordChangeFormData = PasswordChangeFormData(),
    val passwordChangeInProgress: Boolean = false
)

data class PasswordChangeFormData(
    val currentPassword: String = "",
    val newPassword: String = "",
    val newPasswordConfirm: String = ""
)
