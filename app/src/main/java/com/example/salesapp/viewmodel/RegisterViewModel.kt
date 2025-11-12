// Vị trí: .../viewmodel/RegisterViewModel.kt
package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.dto.RegisterDto
import com.example.salesapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    // Các hàm onValueChange
    fun onUsernameChange(value: String) = run { uiState = uiState.copy(username = value, errorMessage = null) }
    fun onEmailChange(value: String) = run { uiState = uiState.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = run { uiState = uiState.copy(password = value, errorMessage = null) }

    fun register() {
        if (uiState.isLoading) return
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        val dto = RegisterDto(
            username = uiState.username.trim(),
            email = uiState.email.trim(),
            password = uiState.password
        )

        viewModelScope.launch {
            val result = authRepository.register(dto)
            result.fold(
                onSuccess = {
                    uiState = uiState.copy(isLoading = false, registerSuccess = true)
                },
                onFailure = {
                    uiState = uiState.copy(isLoading = false, errorMessage = it.message)
                }
            )
        }
    }
}