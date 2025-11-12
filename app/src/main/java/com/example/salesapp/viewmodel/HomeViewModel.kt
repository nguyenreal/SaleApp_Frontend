// Vị trí: .../viewmodel/HomeViewModel.kt
package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.dto.ProductListItemDto
import com.example.salesapp.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val products: List<ProductListItemDto> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        // Tải sản phẩm ngay khi ViewModel được tạo
        fetchProducts()
    }

    fun fetchProducts() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = productRepository.getProducts()
            result.fold(
                onSuccess = { productList ->
                    uiState = uiState.copy(
                        isLoading = false,
                        products = productList
                    )
                },
                onFailure = { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Lỗi không xác định"
                    )
                }
            )
        }
    }
}