package com.example.sumit.ui.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "ProfileViewModel"

class ProfileViewModel : ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginFormData())
    val loginUiState = _loginUiState.asStateFlow()

    fun updateEmail(email: String) {
        _loginUiState.update { currentState ->
            currentState.copy(
                email = email
            )
        }
    }

    fun updatePassword(password: String) {
        _loginUiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }

    fun signInWithEmailAndPassword() {
        Log.d(TAG, "Email: ${_loginUiState.value.email} Password: ${_loginUiState.value.password}")
    }
}

data class LoginFormData(
    val email: String = "",
    val password: String = ""
)
