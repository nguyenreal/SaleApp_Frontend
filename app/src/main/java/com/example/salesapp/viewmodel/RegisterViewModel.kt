// Vị trí: .../viewmodel/RegisterViewModel.kt
package com.example.salesapp.viewmodel

import android.util.Patterns // <-- Cần import để validate email
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
    val confirmPassword: String = "", // <-- THÊM MỚI: Nhập lại mật khẩu
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String? = null, // Lỗi chung từ server

    // Lỗi cho từng trường (field)
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    // Các hàm onValueChange (đã cập nhật để xóa lỗi của từng trường)
    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value, usernameError = null, errorMessage = null)
    }
    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value, emailError = null, errorMessage = null)
    }
    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, passwordError = null, errorMessage = null)
    }
    // <-- THÊM MỚI -->
    fun onConfirmPasswordChange(value: String) {
        uiState = uiState.copy(confirmPassword = value, confirmPasswordError = null, errorMessage = null)
    }

    // Hàm kiểm tra validate
    private fun validateInput(): Boolean {
        var isValid = true
        var usernameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        // 1. Kiểm tra Username
        if (uiState.username.isBlank()) {
            usernameError = "Tên đăng nhập không được để trống"
            isValid = false
        }

        // 2. Kiểm tra Email
        if (uiState.email.isBlank()) {
            emailError = "Email không được để trống"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(uiState.email.trim()).matches()) {
            emailError = "Định dạng email không hợp lệ"
            isValid = false
        }

        // 3. Kiểm tra Password
        if (uiState.password.isBlank()) {
            passwordError = "Mật khẩu không được để trống"
            isValid = false
        } else if (uiState.password.length < 6) {
            passwordError = "Mật khẩu phải có ít nhất 6 ký tự"
            isValid = false
        }

        // 4. Kiểm tra Confirm Password
        if (uiState.confirmPassword != uiState.password) {
            confirmPasswordError = "Mật khẩu nhập lại không khớp"
            isValid = false
        }

        // Cập nhật lại UI state với các lỗi (nếu có)
        uiState = uiState.copy(
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            errorMessage = null // Xóa lỗi chung của server
        )
        return isValid
    }

    fun register() {
        // Bước 1: Kiểm tra validate trước khi gọi API
        if (!validateInput()) {
            return // Dừng lại nếu validate thất bại
        }

        if (uiState.isLoading) return
        // Bước 2: Đặt trạng thái loading
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        val dto = RegisterDto(
            username = uiState.username.trim(),
            email = uiState.email.trim(),
            password = uiState.password
        )

        // Bước 3: Gọi coroutine
        viewModelScope.launch {
            val result = authRepository.register(dto)
            result.fold(
                onSuccess = {
                    uiState = uiState.copy(isLoading = false, registerSuccess = true)
                },
                onFailure = {
                    // Hiển thị lỗi trả về từ server
                    uiState = uiState.copy(isLoading = false, errorMessage = it.message)
                }
            )
        }
    }
}