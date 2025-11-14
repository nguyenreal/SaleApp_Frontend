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
    private val signalRService: SignalRService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(ConversationUiState())
        private set

    private val otherUserId: Int = savedStateHandle.get<String>("userId")?.toIntOrNull() ?: 0

    init {
        fetchHistory()
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

    private fun listenForMessages() {
        signalRService.messageFlow
            .onEach { newMessage ->
                // Chỉ thêm tin nhắn nếu nó thuộc về cuộc trò chuyện này
                if (newMessage.senderID == otherUserId || newMessage.recipientID == otherUserId) {
                    if (uiState.messages.none { it.chatMessageID == newMessage.chatMessageID }) {
                        uiState = uiState.copy(
                            messages = uiState.messages + newMessage
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onMessageChange(newMessage: String) {
        uiState = uiState.copy(currentMessage = newMessage)
    }

    fun sendMessage() {
        val messageText = uiState.currentMessage.trim()
        if (messageText.isEmpty() || otherUserId == 0) return

        // Lưu lại tin nhắn đang gõ, phòng trường hợp gửi lỗi
        val originalMessage = uiState.currentMessage
        // Xóa text khỏi ô input ngay lập tức
        uiState = uiState.copy(currentMessage = "")

        viewModelScope.launch {
            // Gửi qua REST API
            val result = chatRepository.sendMessage(otherUserId, messageText)

            // <<< SỬA LỖI CHAT TẠI ĐÂY >>>
            result.onSuccess { sentMessageDto ->
                // Thêm tin nhắn trả về từ API vào danh sách
                // (SignalR có thể cũng sẽ gửi về, hàm listenForMessages đã check trùng)
                uiState = uiState.copy(
                    messages = uiState.messages + sentMessageDto
                )
            }.onFailure {
                // Gửi thất bại, trả lại tin nhắn cho người dùng gõ
                uiState = uiState.copy(currentMessage = originalMessage)
                // (Bạn có thể thêm 1 dòng uiState = uiState.copy(errorMessage = "Gửi lỗi") ở đây)
            }
        }
    }
}