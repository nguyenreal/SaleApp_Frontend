package com.example.salesapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.salesapp.data.remote.dto.ProductDetailDto
import com.example.salesapp.data.repository.CartRepository
import com.example.salesapp.data.repository.ProductRepository
import com.example.salesapp.workers.CartBadgeWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val product: ProductDetailDto? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isAddingToCart: Boolean = false,
    val addToCartSuccess: Boolean = false,
    val addToCartError: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context // <-- Phải có
) : ViewModel() {

    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    private val workManager = WorkManager.getInstance(context) // <-- Phải có

    private val productId: Int = savedStateHandle.get<String>("productId")!!.toInt()

    init {
        loadProductDetails()
    }

    fun loadProductDetails() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val result = productRepository.getProductDetail(productId)
            result.onSuccess { productDto ->
                uiState = uiState.copy(isLoading = false, product = productDto)
            }.onFailure { exception ->
                uiState = uiState.copy(isLoading = false, errorMessage = exception.message)
            }
        }
    }

    fun addToCart(productId: Int, quantity: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isAddingToCart = true, addToCartError = null, addToCartSuccess = false)
            val result = cartRepository.addItemToCart(productId, quantity)

            result.onSuccess {
                uiState = uiState.copy(isAddingToCart = false, addToCartSuccess = true)
                // Lên lịch cho Worker chạy
                triggerBadgeUpdateWorker() // <-- Phải có

            }.onFailure { exception ->
                uiState = uiState.copy(isAddingToCart = false, addToCartError = exception.message)
            }
        }
    }

    fun clearAddToCartStatus() {
        uiState = uiState.copy(
            addToCartSuccess = false,
            addToCartError = null
        )
    }

    private fun triggerBadgeUpdateWorker() {
        val badgeUpdateWork = OneTimeWorkRequestBuilder<CartBadgeWorker>().build()
        workManager.enqueue(badgeUpdateWork)
    }
}