// Vị trí: .../viewmodel/ProductDetailViewModel.kt
package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.dto.ProductDetailDto
import com.example.salesapp.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: ProductDetailDto? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    // SavedStateHandle dùng để lấy tham số từ navigation (ví dụ: "productId")
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(ProductDetailUiState())
        private set

    init {
        // Tự động lấy "productId" từ route
        savedStateHandle.get<String>("productId")?.let { productId ->
            // Chuyển sang Int và gọi API
            fetchProductDetail(productId.toIntOrNull())
        }
    }

    private fun fetchProductDetail(productId: Int?) {
        if (productId == null) {
            uiState = uiState.copy(isLoading = false, errorMessage = "ID sản phẩm không hợp lệ")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = productRepository.getProductDetail(productId)
            result.fold(
                onSuccess = { product ->
                    uiState = uiState.copy(isLoading = false, product = product)
                },
                onFailure = { error ->
                    uiState = uiState.copy(isLoading = false, errorMessage = error.message)
                }
            )
        }
    }
}