// Vị trí: .../ui/screens/conversation/ConversationScreen.kt
package com.example.salesapp.ui.screens.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.salesapp.data.remote.dto.ChatMessageDto
import com.example.salesapp.viewmodel.ConversationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    otherUserId: Int,
    otherUsername: String,
    viewModel: ConversationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val listState = rememberLazyListState() // Để tự động cuộn

    // Tự động cuộn xuống tin nhắn mới nhất
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(otherUsername, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        },
        // Ô nhập liệu ở dưới cùng
        bottomBar = {
            MessageInputBar(
                value = uiState.currentMessage,
                onValueChange = { viewModel.onMessageChange(it) },
                onSendClick = { viewModel.sendMessage() }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.errorMessage != null -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                    Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
            // Hiển thị bong bóng chat
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues), // Rất quan trọng
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.messages) { message ->
                        // Xác định xem tin nhắn này có phải của "tôi" không
                        // (Vì chúng ta không có UserID của mình, ta giả định
                        // tin nhắn không phải của otherUser thì là của mình)
                        val isMine = message.senderID != otherUserId
                        MessageBubble(message = message, isMine = isMine)
                    }
                }
            }
        }
    }
}

// Bong bóng chat
@Composable
fun MessageBubble(message: ChatMessageDto, isMine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMine) 16.dp else 0.dp,
                        bottomEnd = if (isMine) 0.dp else 16.dp
                    )
                )
                .background(
                    if (isMine) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(message.message)
        }
    }
}

// Thanh nhập liệu
@Composable
fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhập tin nhắn...") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(onClick = onSendClick, enabled = value.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, "Gửi")
            }
        }
    }
}