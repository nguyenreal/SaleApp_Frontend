package com.example.salesapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.dto.CartDto
import com.example.salesapp.data.repository.CartRepository
import com.example.salesapp.utils.BadgeManager
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
    @ApplicationContext private val context: Context
) : ViewModel() {

    var uiState by mutableStateOf(CartUiState())
        private set

    init {
        fetchCart()
    }

    fun fetchCart() {
        viewModelScope.launch {
            val result = cartRepository.getMyCart()
            result.fold(
                onSuccess = { cart ->
                    uiState = uiState.copy(isLoading = false, cart = cart, errorMessage = null)

                    val itemCount = cart.items.sumOf { it.quantity }
                    BadgeManager.updateBadge(context, itemCount)
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
                    // Fetch lại để lấy dữ liệu chính xác
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
                    // Fetch lại để lấy dữ liệu chính xác
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
}