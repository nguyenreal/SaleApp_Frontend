// Vị trí: .../viewmodel/ConversationViewModel.kt
package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.SignalRService
import com.example.salesapp.data.remote.dto.ChatMessageDto
import com.example.salesapp.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationUiState(
    val isLoading: Boolean = true,
    val messages: List<ChatMessageDto> = emptyList(),
    val errorMessage: String? = null,
    val currentMessage: String = "" // Nội dung đang gõ
)

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val signalRService: SignalRService, // Tiêm SignalR
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(ConversationUiState())
        private set

    // Lấy ID của người kia từ navigation
    private val otherUserId: Int = savedStateHandle.get<String>("userId")?.toIntOrNull() ?: 0

    init {
        // 1. Tải lịch sử chat
        fetchHistory()

        // 2. Lắng nghe tin nhắn real-time
        listenForMessages()
    }

    private fun fetchHistory() {
        if (otherUserId == 0) return
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = chatRepository.getConversationHistory(otherUserId)
            result.fold(
                onSuccess = { history ->
                    uiState = uiState.copy(isLoading = false, messages = history)
                },
                onFailure = { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }

    // Lắng nghe tin nhắn mới từ SignalR
    private fun listenForMessages() {
        signalRService.messageFlow
            .onEach { newMessage ->
                // Chỉ thêm tin nhắn nếu nó thuộc về cuộc trò chuyện này
                if (newMessage.senderID == otherUserId || newMessage.recipientID == otherUserId) {
                    // Thêm tin nhắn mới vào cuối danh sách
                    uiState = uiState.copy(
                        messages = uiState.messages + newMessage
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // Cập nhật nội dung đang gõ
    fun onMessageChange(newMessage: String) {
        uiState = uiState.copy(currentMessage = newMessage)
    }

    // Gửi tin nhắn
    fun sendMessage() {
        val messageText = uiState.currentMessage.trim()
        if (messageText.isEmpty() || otherUserId == 0) return

        // Xóa text khỏi ô input
        uiState = uiState.copy(currentMessage = "")

        viewModelScope.launch {
            // Gửi qua REST API
            // Backend sẽ tự động đẩy tin nhắn này về cho chúng ta (và người kia)
            // qua SignalR, hàm listenForMessages() sẽ bắt được nó.
            chatRepository.sendMessage(otherUserId, messageText)
            // (Chúng ta có thể xử lý lỗi ở đây nếu muốn)
        }
    }
}