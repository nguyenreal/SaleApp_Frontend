// Vị trí: .../viewmodel/CartViewModel.kt
package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.dto.CartDto
import com.example.salesapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val isLoading: Boolean = true,
    val cart: CartDto? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    var uiState by mutableStateOf(CartUiState())
        private set

    init {
        // Tải giỏ hàng ngay khi vào màn hình
        fetchCart()
    }

    fun fetchCart() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = cartRepository.getMyCart()
            result.fold(
                onSuccess = { cart ->
                    uiState = uiState.copy(isLoading = false, cart = cart)
                },
                onFailure = { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }

    fun updateQuantity(cartItemId: Int, newQuantity: Int) {
        // Không cho phép số lượng < 1
        if (newQuantity <= 0) return

        uiState = uiState.copy(isLoading = true) // Hiển thị loading
        viewModelScope.launch {
            val result = cartRepository.updateItemQuantity(cartItemId, newQuantity)
            // Cập nhật lại UI sau khi thành công (hoặc thất bại)
            result.fold(
                onSuccess = { uiState = uiState.copy(isLoading = false, cart = it) },
                onFailure = { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
            )
        }
    }

    fun removeItem(cartItemId: Int) {
        uiState = uiState.copy(isLoading = true) // Hiển thị loading
        viewModelScope.launch {
            val result = cartRepository.removeItemFromCart(cartItemId)
            // Cập nhật lại UI
            result.fold(
                onSuccess = { uiState = uiState.copy(isLoading = false, cart = it) },
                onFailure = { uiState = uiState.copy(isLoading = false, errorMessage = it.message) }
            )
        }
    }
}