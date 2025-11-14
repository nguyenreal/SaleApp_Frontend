// Vị trí: .../data/remote/dto/UserProfileDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

data class UserProfileDto(
    @field:Json(name = "userID") val userID: Int,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "phoneNumber") val phoneNumber: String?,
    @field:Json(name = "address") val address: String?,
    @field:Json(name = "role") val role: String
)