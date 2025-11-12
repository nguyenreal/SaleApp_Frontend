// Vị trí: .../data/remote/dto/ProductDetailDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

data class ProductDetailDto(
    @field:Json(name = "productID") val productID: Int,
    @field:Json(name = "productName") val productName: String,
    @field:Json(name = "briefDescription") val briefDescription: String?,
    @field:Json(name = "fullDescription") val fullDescription: String?,
    @field:Json(name = "technicalSpecifications") val technicalSpecifications: String?,
    @field:Json(name = "price") val price: Double,
    @field:Json(name = "imageURL") val imageURL: String?,
    @field:Json(name = "categoryName") val categoryName: String?
)