package com.example.sumit.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.users.UserRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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
        Log.d(
            TAG,
            "Email: ${_loginUiState.value.form.email} " +
                    "Password: ${_loginUiState.value.form.password}"
        )

        if (_loginUiState.value.form.email.isEmpty() || _loginUiState.value.form.password.isEmpty()) {
            return
        }

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

                resetLoginForm()
            } catch (e: FirebaseException) {
                Log.e(TAG, "Login error", e)
                setMessage("Incorrect email address or password")
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

    private fun resetPasswordChangeForm() {
        _profileUiState.update { currentState ->
            currentState.copy(
                form = PasswordChangeFormData()
            )
        }
    }

    fun togglePasswordChangeFormVisibility() {
        _profileUiState.update { currentState ->
            currentState.copy(
                passwordChangeVisible = !currentState.passwordChangeVisible
            )
        }
    }

    fun changePassword() {
        Log.d(
            TAG,
            "Old password: ${_profileUiState.value.form.currentPassword} " +
                    "New password: ${_profileUiState.value.form.newPassword}"
        )

        if (_profileUiState.value.form.currentPassword.isEmpty() || _profileUiState.value.form.newPassword.isEmpty()) {
            setMessage("Please fill out every field")
            return
        }

        if (_profileUiState.value.form.newPassword != _profileUiState.value.form.newPasswordConfirm) {
            setMessage("The new password and the confirm password must match")
            return
        }

        viewModelScope.launch {
            setPasswordChangeInProgress(true)

            try {
                userRepository.changePassword(
                    _profileUiState.value.email,
                    _profileUiState.value.form.currentPassword,
                    _profileUiState.value.form.newPassword
                )

                _profileUiState.update { currentState ->
                    currentState.copy(
                        passwordChangeVisible = false
                    )
                }

                setMessage("Password changed successfully")

                resetPasswordChangeForm()
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.e(TAG, "Change password invalid credentials")
                setMessage("Incorrect password")
            } catch (e: FirebaseException) {
                Log.e(TAG, "Change password error", e)
                setMessage("Password change failed")
            }

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

        resetPasswordChangeForm()
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
    val passwordChangeVisible: Boolean = false,
    val passwordChangeInProgress: Boolean = false
)

data class PasswordChangeFormData(
    val currentPassword: String = "",
    val newPassword: String = "",
    val newPasswordConfirm: String = ""
)
