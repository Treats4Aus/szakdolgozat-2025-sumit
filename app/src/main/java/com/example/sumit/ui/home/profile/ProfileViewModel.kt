package com.example.sumit.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.R
import com.example.sumit.data.translations.TranslationsRepository
import com.example.sumit.data.users.UserRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val translationsRepository: TranslationsRepository
) : ViewModel() {
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
        val form = _loginUiState.value.form

        Log.d(TAG, "Email: ${form.email} Password: ${form.password}")

        if (form.email.isEmpty() || form.password.isEmpty()) {
            return
        }

        viewModelScope.launch {
            setLoginInProgress(true)

            try {
                userRepository.signInWithEmailAndPassword(form.email, form.password)

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
                setMessage(translationsRepository.getTranslation(R.string.incorrect_email_or_password))
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
        val form = _profileUiState.value.form

        Log.d(TAG, "Old password: ${form.currentPassword} New password: ${form.newPassword}")

        if (form.currentPassword.isEmpty() || form.newPassword.isEmpty()) {
            setMessage(translationsRepository.getTranslation(R.string.please_fill_out_every_field))
            return
        }

        if (form.newPassword != form.newPasswordConfirm) {
            setMessage(translationsRepository.getTranslation(R.string.password_and_confirm_must_match))
            return
        }

        viewModelScope.launch {
            setPasswordChangeInProgress(true)

            try {
                val userEmail = _profileUiState.value.email

                userRepository.changePassword(userEmail, form.currentPassword, form.newPassword)

                _profileUiState.update { currentState ->
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
