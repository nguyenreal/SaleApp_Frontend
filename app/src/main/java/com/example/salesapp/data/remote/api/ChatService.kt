// Vị trí: .../data/remote/api/ChatService.kt
package com.example.salesapp.data.remote.api

import com.example.salesapp.data.remote.dto.ChatMessageDto
import com.example.salesapp.data.remote.dto.SendChatMessageDto
import com.example.salesapp.data.remote.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatService {

    // GET /api/Chat/list (Lấy danh sách các cuộc trò chuyện)
    @GET("api/Chat/list")
    suspend fun getChatList(): Response<List<UserProfileDto>>

    // GET /api/Chat/history/{otherUserId} (Lấy lịch sử chat với 1 người)
    @GET("api/Chat/history/{otherUserId}")
    suspend fun getConversationHistory(
        @Path("otherUserId") otherUserId: Int
    ): Response<List<ChatMessageDto>>

    @POST("api/Chat/send")
    suspend fun sendChatMessage(
        @Body request: SendChatMessageDto
    ): Response<ChatMessageDto>
}