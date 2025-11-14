// Vị trí: .../data/repository/ProductRepository.kt
package com.example.salesapp.data.repository

import com.example.salesapp.data.remote.api.ProductService
import com.example.salesapp.data.remote.dto.ErrorResponse // <-- Thêm
import com.example.salesapp.data.remote.dto.ProductDetailDto // <-- Thêm
import com.example.salesapp.data.remote.dto.ProductListItemDto
import com.squareup.moshi.Moshi // <-- Thêm
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productService: ProductService,
    private val moshi: Moshi // <-- Inject Moshi
) {
    suspend fun getProducts(): Result<List<ProductListItemDto>> {
        return try {
            val response = productService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // <-- SỬA LẠI: Dùng trình parse lỗi -->
                val errorMessage = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // --- THÊM HÀM MỚI NÀY ---
    suspend fun getProductDetail(productId: Int): Result<ProductDetailDto> {
        return try {
            val response = productService.getProductById(productId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // --- THÊM HÀM HELPER NÀY (Copy từ AuthRepository) ---
    private fun parseErrorResponse(errorBody: String?): String {
        if (errorBody == null) return "Lỗi không xác định"

        return try {
            val adapter = moshi.adapter(ErrorResponse::class.java)
            val errorResponse = adapter.fromJson(errorBody)

            if (errorResponse?.errors != null) {
                return errorResponse.errors.entries.joinToString("\n") {
                    "${it.key}: ${it.value.firstOrNull()}"
                }
            }
            errorResponse?.message ?: "Lỗi Bad Request"
        } catch (e: Exception) {
            "Lỗi không thể đọc phản hồi (Bad Request)"
        }
    }
}