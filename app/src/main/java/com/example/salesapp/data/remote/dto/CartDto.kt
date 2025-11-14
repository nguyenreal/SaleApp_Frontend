// Vị trí: .../dto/CartDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

// Đây là object trả về sau khi thêm vào giỏ hàng thành công
data class CartDto(
    @field:Json(name = "cartID") val cartID: Int,
    @field:Json(name = "userID") val userID: Int?,
    @field:Json(name = "username") val username: String?,
    @field:Json(name = "totalPrice") val totalPrice: Double,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "items") val items: List<CartItemDto> = emptyList()
)