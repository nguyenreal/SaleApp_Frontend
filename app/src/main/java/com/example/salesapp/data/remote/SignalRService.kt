package com.example.salesapp.data.remote

import android.util.Log
import com.example.salesapp.data.local.UserPreferencesRepository
import com.example.salesapp.data.remote.dto.ChatMessageDto
import com.example.salesapp.data.remote.dto.SendChatMessageDto
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single // <<< THÊM IMPORT NÀY
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRService @Inject constructor(
    private val prefsRepository: UserPreferencesRepository
) {
    private var hubConnection: HubConnection? = null

    private val _messageFlow = MutableSharedFlow<ChatMessageDto>()
    val messageFlow = _messageFlow.asSharedFlow()

    private val HUB_URL = "http://10.0.2.2:5281/chathub"

    fun initConnection() {
        if (hubConnection != null && hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            return
        }

        hubConnection = HubConnectionBuilder
            .create(HUB_URL)
            .withAccessTokenProvider(
                Single.fromCallable {
                    runBlocking {
                        prefsRepository.authToken.first() ?: ""
                    }
                }
            )
            .build()

        hubConnection?.on("ReceiveMessage", { message ->
            Log.d("SignalR", "Message received: $message")
            runBlocking { _messageFlow.emit(message) }
        }, ChatMessageDto::class.java)

        // Bắt đầu kết nối
        try {
            hubConnection?.start()?.blockingAwait()
            Log.d("SignalR", "Connection Started!")
        } catch (e: Exception) {
            Log.e("SignalR", "Connection failed: ${e.message}")
        }
    }


    // Đóng kết nối
    fun closeConnection() {
        hubConnection?.stop()
        hubConnection = null
        Log.d("SignalR", "Connection Closed.")
    }
}