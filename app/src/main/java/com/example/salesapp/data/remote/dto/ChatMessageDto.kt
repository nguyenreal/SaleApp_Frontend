// Vị trí: .../data/remote/dto/ChatMessageDto.kt
package com.example.salesapp.data.remote.dto
import com.squareup.moshi.Json

data class ChatMessageDto(
    @field:Json(name = "chatMessageID") val chatMessageID: Int,
    @field:Json(name = "senderID") val senderID: Int,
    @field:Json(name = "senderUsername") val senderUsername: String,
    @field:Json(name = "recipientID") val recipientID: Int,
    @field:Json(name = "recipientUsername") val recipientUsername: String,
    @field:Json(name = "message") val message: String,
    @field:Json(name = "sentAt") val sentAt: String // Dùng String cho đơn giản, có thể parse sau
)