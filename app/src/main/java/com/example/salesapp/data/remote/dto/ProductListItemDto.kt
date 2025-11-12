// Vị trí: .../data/remote/dto/ProductListItemDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

data class ProductListItemDto(
    @field:Json(name = "productID") val productID: Int,
    @field:Json(name = "productName") val productName: String,
    @field:Json(name = "briefDescription") val briefDescription: String?,
    @field:Json(name = "price") val price: Double, // Moshi tự xử lý decimal
    @field:Json(name = "imageURL") val imageURL: String?,
    @field:Json(name = "categoryName") val categoryName: String?
)