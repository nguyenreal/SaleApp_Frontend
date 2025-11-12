// Vị trí: .../data/repository/AuthRepository.kt
package com.example.salesapp.data.repository

import com.example.salesapp.data.local.UserPreferencesRepository
import com.example.salesapp.data.remote.api.AuthService
import com.example.salesapp.data.remote.dto.*
import com.squareup.moshi.Moshi // <-- THÊM IMPORT NÀY
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val prefsRepository: UserPreferencesRepository,
    private val moshi: Moshi // <-- INJECT MOSHI VÀO ĐÂY
) {

    // (Hàm authToken và login... chúng ta sẽ sửa login sau)
    val authToken: Flow<String?> = prefsRepository.authToken

    // --- HÀM LOGIN (CŨNG SỬA LẠI) ---
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginDto(username = username, password = password)
            val response = authService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                prefsRepository.saveAuthInfo(authResponse.token, authResponse.role)
                Result.success(authResponse)
            } else {
                // --- SỬA LẠI KHỐI NÀY ---
                val errorMessage = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // --- HÀM REGISTER (SỬA LẠI) ---
    suspend fun register(dto: RegisterDto): Result<RegisterResponse> {
        return try {
            val response = authService.register(dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // --- SỬA LẠI KHỐI NÀY ---
                // Parse lỗi JSON từ server
                val errorMessage = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // --- HÀM HELPER ĐỂ PARSE LỖI ---
    private fun parseErrorResponse(errorBody: String?): String {
        if (errorBody == null) return "Lỗi không xác định"

        return try {
            val adapter = moshi.adapter(ErrorResponse::class.java)
            val errorResponse = adapter.fromJson(errorBody)

            // Nếu có lỗi validation chi tiết, hãy hiển thị chúng
            if (errorResponse?.errors != null) {
                // Nối các lỗi lại, ví dụ: "Password: Tối thiểu 6 ký tự"
                return errorResponse.errors.entries.joinToString("\n") {
                    "${it.key}: ${it.value.firstOrNull()}"
                }
            }
            // Nếu chỉ có message chính (ví dụ: "Username already exists")
            errorResponse?.message ?: "Lỗi Bad Request"
        } catch (e: Exception) {
            "Lỗi không thể đọc phản hồi (Bad Request)"
        }
    }
}