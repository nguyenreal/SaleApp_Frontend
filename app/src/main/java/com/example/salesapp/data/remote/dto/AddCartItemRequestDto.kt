// Vị trí: .../dto/AddCartItemRequestDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

data class AddCartItemRequestDto(
    @field:Json(name = "productId") val productId: Int,
    @field:Json(name = "quantity") val quantity: Int
)