// Vị trí: .../data/repository/ProductRepository.kt
package com.example.salesapp.data.repository

import com.example.salesapp.data.remote.api.ProductService
import com.example.salesapp.data.remote.dto.ProductListItemDto
import javax.inject.Inject
import javax.inject.Singleton

// (Chúng ta sẽ sớm thêm hàm parseErrorResponse chung vào đây)
@Singleton
class ProductRepository @Inject constructor(
    private val productService: ProductService
) {
    suspend fun getProducts(): Result<List<ProductListItemDto>> {
        return try {
            val response = productService.getProducts() // Tạm gọi không filter
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Tải sản phẩm thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // (Thêm hàm getProductDetail(id) ở đây sau)
}