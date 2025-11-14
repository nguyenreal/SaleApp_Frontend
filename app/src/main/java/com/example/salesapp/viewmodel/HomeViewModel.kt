package com.example.salesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesapp.data.remote.dto.CategoryDto
import com.example.salesapp.data.remote.dto.ProductListItemDto
import com.example.salesapp.data.repository.CategoryRepository
import com.example.salesapp.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Enum cho sort options
 */
enum class SortOption(val value: String, val displayName: String) {
    NAME_ASC("name_asc", "Tên A-Z"),
    NAME_DESC("name_desc", "Tên Z-A"),
    PRICE_ASC("price_asc", "Giá thấp đến cao"),
    PRICE_DESC("price_desc", "Giá cao đến thấp")
}

/**
 * Filter state
 */
data class FilterState(
    val selectedCategoryId: Int? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val selectedSortOption: SortOption = SortOption.NAME_ASC
)

/**
 * UI State
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val products: List<ProductListItemDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val errorMessage: String? = null,
    val filterState: FilterState = FilterState()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadCategories()
        fetchProducts()
    }

    /**
     * Load danh sách categories để hiển thị filter
     */
    private fun loadCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            result.onSuccess { categories ->
                uiState = uiState.copy(categories = categories)
            }.onFailure { error ->
                // Log error nhưng không block UI
                // Categories là optional feature
            }
        }
    }

    /**
     * Fetch products với filter/sort hiện tại
     */
    fun fetchProducts() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val filterState = uiState.filterState

            val result = productRepository.getProducts(
                categoryId = filterState.selectedCategoryId,
                minPrice = filterState.minPrice,
                maxPrice = filterState.maxPrice,
                sortBy = filterState.selectedSortOption.value
            )

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

    /**
     * Apply category filter
     */
    fun filterByCategory(categoryId: Int?) {
        uiState = uiState.copy(
            filterState = uiState.filterState.copy(selectedCategoryId = categoryId)
        )
        fetchProducts()
    }

    /**
     * Apply price range filter
     */
    fun filterByPriceRange(minPrice: Double?, maxPrice: Double?) {
        uiState = uiState.copy(
            filterState = uiState.filterState.copy(
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        )
        fetchProducts()
    }

    /**
     * Apply sort option
     */
    fun sortBy(sortOption: SortOption) {
        uiState = uiState.copy(
            filterState = uiState.filterState.copy(selectedSortOption = sortOption)
        )
        fetchProducts()
    }

    /**
     * Clear tất cả filters
     */
    fun clearFilters() {
        uiState = uiState.copy(filterState = FilterState())
        fetchProducts()
    }

    /**
     * Check xem có filter nào đang active không
     */
    fun hasActiveFilters(): Boolean {
        val filterState = uiState.filterState
        return filterState.selectedCategoryId != null ||
                filterState.minPrice != null ||
                filterState.maxPrice != null ||
                filterState.selectedSortOption != SortOption.NAME_ASC
    }
}