// Vị trí: .../repository/ChatRepository.kt
package com.example.salesapp.data.repository

import com.example.salesapp.data.remote.api.ChatService
import com.example.salesapp.data.remote.dto.ChatMessageDto
import com.example.salesapp.data.remote.dto.ErrorResponse
import com.example.salesapp.data.remote.dto.SendChatMessageDto
import com.example.salesapp.data.remote.dto.UserProfileDto
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatService: ChatService,
    private val moshi: Moshi
) {
    // Lấy danh sách các cuộc trò chuyện
    suspend fun getChatList(): Result<List<UserProfileDto>> {
        return try {
            val response = chatService.getChatList()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(parseErrorResponse(response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // Lấy lịch sử chat với 1 người
    suspend fun getConversationHistory(otherUserId: Int): Result<List<ChatMessageDto>> {
        return try {
            val response = chatService.getConversationHistory(otherUserId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(parseErrorResponse(response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    suspend fun sendMessage(recipientId: Int, message: String): Result<ChatMessageDto> {
        return try {
            val request = SendChatMessageDto(recipientId, message)
            val response = chatService.sendChatMessage(request)

            if (response.isSuccessful && response.body() != null) {
                // Thành công! Backend đã lưu và tự đẩy SignalR
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(parseErrorResponse(response.errorBody()?.string())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // (Hàm parse lỗi copy từ các Repo khác)
    private fun parseErrorResponse(errorBody: String?): String {
        if (errorBody == null) return "Lỗi không xác định"

        return try {
            val adapter = moshi.adapter(ErrorResponse::class.java)

            // <<< DÒNG BẠN BỊ THIẾU LÀ DÒNG NÀY >>>
            val errorResponse = adapter.fromJson(errorBody)

            // Xử lý lỗi validation (nếu có)
            if (errorResponse?.errors != null) {
                return errorResponse.errors.entries.joinToString("\n") {
                    "${it.key}: ${it.value.firstOrNull()}"
                }
            }
            // Xử lý lỗi message chung
            errorResponse?.message ?: "Lỗi Bad Request"
        } catch (e: Exception) {
            "Lỗi không thể đọc phản hồi (Bad Request)"
        }
    }
}