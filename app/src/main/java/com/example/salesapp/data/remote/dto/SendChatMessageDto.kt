// Vị trí: .../data/remote/dto/SendChatMessageDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

data class SendChatMessageDto(
    @field:Json(name = "recipientID") val recipientID: Int,
    @field:Json(name = "message") val message: String
)