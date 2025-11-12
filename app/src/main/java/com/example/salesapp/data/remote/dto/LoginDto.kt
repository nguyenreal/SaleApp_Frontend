// Vị trí: .../com/example/salesapp/data/remote/dto/LoginDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

// Khớp với LoginDto.cs
data class LoginDto(
    @field:Json(name = "username") val username: String,
    @field:Json(name = "password") val password: String
)