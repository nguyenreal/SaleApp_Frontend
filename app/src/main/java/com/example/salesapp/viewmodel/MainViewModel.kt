// Vị trí: .../viewmodel/MainViewModel.kt
package com.example.salesapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.salesapp.data.remote.SignalRService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val signalRService: SignalRService // Tiêm SignalR Service
) : ViewModel() {

    init {
        // Khởi động kết nối SignalR ngay khi MainViewModel được tạo
        signalRService.initConnection()
    }

    override fun onCleared() {
        // Đóng kết nối khi app bị đóng hoàn toàn
        signalRService.closeConnection()
        super.onCleared()
    }
}