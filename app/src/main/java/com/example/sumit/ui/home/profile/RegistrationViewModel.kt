package com.example.sumit.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sumit.data.users.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "RegistrationViewModel"

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registrationUiState = MutableStateFlow(RegistrationUiState())
    val registrationUiState = _registrationUiState.asStateFlow()

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
        Log.d(
            TAG,
            "Email: ${_registrationUiState.value.form.email} " +
                    "Password: ${_registrationUiState.value.form.password}"
        )

        viewModelScope.launch {
            setRegistrationState(RegistrationState.Loading)

            userRepository.registerWithEmailAndPassword(
                _registrationUiState.value.form.email,
                _registrationUiState.value.form.password
            )

            setRegistrationState(RegistrationState.Finished)
        }
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
