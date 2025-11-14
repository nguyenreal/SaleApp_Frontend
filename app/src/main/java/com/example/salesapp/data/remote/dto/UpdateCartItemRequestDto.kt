// Vị trí: .../dto/UpdateCartItemRequestDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

// Khớp với UpdateCartItemRequestDto.cs
data class UpdateCartItemRequestDto(
    @field:Json(name = "quantity") val quantity: Int
)