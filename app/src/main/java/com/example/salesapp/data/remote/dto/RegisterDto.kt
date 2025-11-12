// Vị trí: .../dto/RegisterDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

// Khớp với RegisterDto.cs
data class RegisterDto(
    @field:Json(name = "username") val username: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String,
    @field:Json(name = "phoneNumber") val phoneNumber: String? = null,
    @field:Json(name = "address") val address: String? = null
)