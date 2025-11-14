// Vị trí: .../dto/CartItemDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

data class CartItemDto(
    @field:Json(name = "cartItemID") val cartItemID: Int,
    @field:Json(name = "productID") val productID: Int?,
    @field:Json(name = "productName") val productName: String?,
    @field:Json(name = "imageURL") val imageURL: String?, // <<< THÊM DÒNG NÀY
    @field:Json(name = "quantity") val quantity: Int,
    @field:Json(name = "price") val price: Double
)