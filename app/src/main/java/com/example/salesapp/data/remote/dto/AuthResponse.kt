// Vị trí: .../com/example/salesapp/data/remote/dto/AuthResponse.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

// Khớp 100% với AuthResponseDto.cs của bạn
data class AuthResponse(
    @field:Json(name = "token") val token: String,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "role") val role: String
)