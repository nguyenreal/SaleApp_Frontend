// Vị trí: .../com/example/salesapp/viewmodel/LoginViewModel.kt
package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lớp chứa trạng thái của màn hình Login
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository // Hilt tự động "tiêm" vào đây
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, errorMessage = null)
    }

    fun login() {
        if (uiState.isLoading) return // Chặn gọi nhiều lần

        // <<< SỬA LỖI VALIDATION TẠI ĐÂY >>>
        val username = uiState.username.trim()
        val password = uiState.password.trim()

        if (username.isBlank() || password.isBlank()) {
            uiState = uiState.copy(
                errorMessage = "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu."
            )
            return // Dừng, không gọi API
        }
        // <<< KẾT THÚC SỬA LỖI >>>

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            // Sử dụng biến đã trim()
            val result = authRepository.login(username, password)

            result.fold(
                onSuccess = { response ->
                    // ĐĂNG NHẬP THÀNH CÔNG (Token đã được lưu)
                    uiState = uiState.copy(
                        isLoading = false,
                        loginSuccess = true
                    )
                },
                onFailure = { error ->
                    // ĐĂNG NHẬP THẤT BẠI
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Lỗi không xác định"
                    )
                }
            )
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}