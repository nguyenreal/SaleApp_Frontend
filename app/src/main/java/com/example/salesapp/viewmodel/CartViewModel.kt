package com.example.salesapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.salesapp.data.remote.dto.CartDto
import com.example.salesapp.data.repository.CartRepository
import com.example.salesapp.workers.CartBadgeWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val isLoading: Boolean = true,
    val cart: CartDto? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    @ApplicationContext private val context: Context // Sửa: Dùng Context
) : ViewModel() {

    var uiState by mutableStateOf(CartUiState())
        private set

    private val workManager = WorkManager.getInstance(context) // Sửa: Thêm WorkManager

    init {
        fetchCart()
    }

    fun fetchCart() {
        // Không set isLoading = true ở đây để tránh giật màn hình khi gọi lại
        // uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = cartRepository.getMyCart()
            result.fold(
                onSuccess = { cart ->
                    uiState = uiState.copy(isLoading = false, cart = cart, errorMessage = null)
                    // CẬP NHẬT BADGE
                    triggerBadgeUpdateWorker() // Gọi Worker
                },
                onFailure = { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }

    fun updateQuantity(cartItemId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(cartItemId)
            return
        }

        // Tạm thời cập nhật UI (giúp mượt hơn)
        uiState = uiState.copy(cart = uiState.cart?.copy(
            items = uiState.cart!!.items.map {
                if(it.cartItemID == cartItemId) it.copy(quantity = newQuantity) else it
            }
        ))

        viewModelScope.launch {
            val result = cartRepository.updateItemQuantity(cartItemId, newQuantity)
            result.fold(
                onSuccess = {
                    // <<< SỬA LỖI LOGIC TẠI ĐÂY >>>
                    // Không tin tưởng 'it' (dữ liệu trả về từ PUT)
                    // Gọi fetchCart() để lấy lại dữ liệu ĐÚNG
                    fetchCart()
                },
                onFailure = {
                    uiState = uiState.copy(isLoading = false, errorMessage = it.message)
                    // Nếu lỗi, fetch lại để khôi phục trạng thái cũ
                    fetchCart()
                }
            )
        }
    }

    fun removeItem(cartItemId: Int) {
        // Tạm thời cập nhật UI (giúp mượt hơn)
        uiState = uiState.copy(cart = uiState.cart?.copy(
            items = uiState.cart!!.items.filter { it.cartItemID != cartItemId }
        ))

        viewModelScope.launch {
            val result = cartRepository.removeItemFromCart(cartItemId)
            result.fold(
                onSuccess = {
                    // <<< SỬA LỖI LOGIC TẠI ĐÂY >>>
                    // Không tin tưởng 'it' (dữ liệu trả về từ DELETE)
                    // Gọi fetchCart() để lấy lại dữ liệu ĐÚNG
                    fetchCart()
                },
                onFailure = {
                    uiState = uiState.copy(isLoading = false, errorMessage = it.message)
                    // Nếu lỗi, fetch lại để khôi phục trạng thái cũ
                    fetchCart()
                }
            )
        }
    }

    // Hàm gọi Worker
    private fun triggerBadgeUpdateWorker() {
        val badgeUpdateWork = OneTimeWorkRequestBuilder<CartBadgeWorker>().build()
        workManager.enqueue(badgeUpdateWork)
    }
}