package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.dto.UserProfileDto
import com.example.salesapp.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// <<< ID HỖ TRỢ ĐƯỢC ĐỊNH NGHĨA Ở ĐÂY >>>
private const val SUPPORT_ACCOUNT_ID = 1 // (Phải khớp với ID Admin/Support ở backend)

data class ChatListUiState(
    val isLoading: Boolean = true,
    val chatList: List<UserProfileDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    var uiState by mutableStateOf(ChatListUiState())
        private set

    init {
        fetchChatList()
    }

    // --- SỬA LẠI HÀM NÀY ---
    fun fetchChatList() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            // 1. Gọi API để lấy danh sách chat thật
            val result = chatRepository.getChatList()

            result.fold(
                onSuccess = { listFromApi ->

                    // --- ĐÂY LÀ LOGIC MỚI ---
                    // 2. Tạo một user "Hỗ trợ Cửa hàng" ảo
                    val supportUser = UserProfileDto(
                        userID = SUPPORT_ACCOUNT_ID,
                        username = "Hỗ trợ Cửa hàng",
                        email = "support@salesapp.com", // Email/SĐT... không quan trọng
                        phoneNumber = null,
                        address = null,
                        role = "Admin"
                    )

                    // 3. Tạo danh sách cuối cùng, ghim Hỗ trợ lên đầu
                    val completeList = mutableListOf(supportUser)

                    // 4. Thêm những người khác vào (lọc bỏ ID Hỗ trợ nếu họ đã chat rồi)
                    completeList.addAll(
                        listFromApi.filter { it.userID != SUPPORT_ACCOUNT_ID }
                    )
                    // --- KẾT THÚC LOGIC MỚI ---

                    uiState = uiState.copy(
                        isLoading = false,
                        chatList = completeList // <-- Sử dụng danh sách đã gộp
                    )
                },
                onFailure = { error ->
                    // Nếu lỗi, vẫn hiển thị Kênh Hỗ trợ
                    val supportUser = UserProfileDto(SUPPORT_ACCOUNT_ID, "Hỗ trợ Cửa hàng", "", null, null, "Admin")
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message,
                        chatList = listOf(supportUser) // Vẫn hiển thị support ngay cả khi lỗi
                    )
                }
            )
        }
    }
}