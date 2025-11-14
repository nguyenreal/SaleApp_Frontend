// Vị trí: .../dto/StoreLocationDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

data class StoreLocationDto(
    @field:Json(name = "locationID") val locationID: Int,
    @field:Json(name = "address") val address: String,
    @field:Json(name = "latitude") val latitude: Double, // Dùng Double
    @field:Json(name = "longitude") val longitude: Double // Dùng Double
)