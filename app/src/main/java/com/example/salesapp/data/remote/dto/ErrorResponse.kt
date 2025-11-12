package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

// Khớp với ErrorResponse.cs
data class ErrorResponse(
    @field:Json(name = "message") val message: String,
    @field:Json(name = "errors") val errors: Map<String, List<String>>? = null
)