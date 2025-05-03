package com.example.sumit.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "RegistrationViewModel"

class RegistrationViewModel : ViewModel() {
    private val _registrationUiState = MutableStateFlow(RegistrationFormData())
    val registrationUiState = _registrationUiState.asStateFlow()

    fun updateEmail(email: String) {
        _registrationUiState.update { currentState ->
            currentState.copy(
                email = email
            )
        }
    }

    fun updateName(name: String) {
        _registrationUiState.update { currentState ->
            currentState.copy(
                name = name
            )
        }
    }

    fun updateUsername(username: String) {
        _registrationUiState.update { currentState ->
            currentState.copy(
                username = username
            )
        }
    }

    fun updatePassword(password: String) {
        _registrationUiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }

    fun updatePasswordConfirm(passwordConfirm: String) {
        _registrationUiState.update { currentState ->
            currentState.copy(
                passwordConfirm = passwordConfirm
            )
        }
    }

    fun registerWithEmailAndPassword() {
        Log.d(TAG, "Email: ${_registrationUiState.value.email} Password: ${_registrationUiState.value.password}")
    }
}

data class RegistrationFormData(
    val email: String = "",
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val passwordConfirm: String = ""
)
