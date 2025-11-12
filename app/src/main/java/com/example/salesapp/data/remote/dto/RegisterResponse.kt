// Vị trí: .../dto/RegisterResponse.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

// Khớp với response: new { message = "User registered successfully" }
data class RegisterResponse(
    @field:Json(name = "message") val message: String
)