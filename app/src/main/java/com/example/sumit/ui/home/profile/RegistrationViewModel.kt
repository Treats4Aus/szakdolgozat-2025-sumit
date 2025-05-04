package com.example.sumit.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.R
import com.example.sumit.data.translations.TranslationsRepository
import com.example.sumit.data.users.UserRepository
import com.example.sumit.utils.PasswordValidator
import com.example.sumit.utils.TranslationPasswordValidator
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "RegistrationViewModel"

class RegistrationViewModel(
    private val userRepository: UserRepository,
    private val translationsRepository: TranslationsRepository
) : ViewModel() {
    private val passwordValidator: PasswordValidator =
        TranslationPasswordValidator(translationsRepository)

    private val _registrationUiState = MutableStateFlow(RegistrationUiState())
    val registrationUiState = _registrationUiState.asStateFlow()

    private val _currentMessageRes = MutableStateFlow("")
    val currentMessageRes = _currentMessageRes.asStateFlow()

    fun updateEmail(email: String) =
        updateForm(_registrationUiState.value.form.copy(email = email))

    fun updateName(name: String) =
        updateForm(_registrationUiState.value.form.copy(name = name))

    fun updateUsername(username: String) =
        updateForm(_registrationUiState.value.form.copy(username = username))

    fun updatePassword(password: String) =
        updateForm(_registrationUiState.value.form.copy(password = password))

    fun updatePasswordConfirm(passwordConfirm: String) =
        updateForm(_registrationUiState.value.form.copy(passwordConfirm = passwordConfirm))

    fun registerWithEmailAndPassword() {
        val form = _registrationUiState.value.form

        Log.d(TAG, "Email: ${form.email} Password: ${form.password}")

        if (
            form.email.isEmpty() || form.name.isEmpty() || form.username.isEmpty() ||
            form.password.isEmpty() || form.passwordConfirm.isEmpty()
        ) {
            setMessage(translationsRepository.getTranslation(R.string.please_fill_out_every_field))
            return
        }

        if (form.username.trim().length < 3) {
            setMessage(translationsRepository.getTranslation(R.string.username_must_be_long))
            return
        }

        val validationResult = passwordValidator.validate(form.password)
        if (validationResult != null) {
            setMessage(validationResult)
            return
        }

        if (form.password != form.passwordConfirm) {
            setMessage(translationsRepository.getTranslation(R.string.password_and_confirm_must_match))
            return
        }

        viewModelScope.launch {
            setRegistrationState(RegistrationState.Loading)

            try {
                userRepository.registerWithEmailAndPassword(form.email, form.password)

                setMessage(translationsRepository.getTranslation(R.string.successful_registration))
            } catch (e: FirebaseException) {
                Log.e(TAG, "Registration failed", e)
                setMessage(translationsRepository.getTranslation(R.string.unsuccessful_registration))
            }

            setRegistrationState(RegistrationState.Finished)
        }
    }

    fun resetMessage() {
        _currentMessageRes.value = ""
    }

    private fun setMessage(message: String) {
        _currentMessageRes.value = message
    }

    private fun updateForm(formData: RegistrationFormData) {
        _registrationUiState.update { currentState ->
            currentState.copy(
                form = formData
            )
        }
    }

    private fun setRegistrationState(state: RegistrationState) {
        _registrationUiState.update { currentState ->
            currentState.copy(
                state = state
            )
        }
    }
}

data class RegistrationUiState(
    val state: RegistrationState = RegistrationState.Default,
    val form: RegistrationFormData = RegistrationFormData()
)

enum class RegistrationState {
    Default,
    Loading,
    Finished
}

data class RegistrationFormData(
    val email: String = "",
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val passwordConfirm: String = ""
)
