// Vị trí: .../repository/CartRepository.kt
package com.example.salesapp.data.repository

import com.example.salesapp.data.remote.api.CartService
import com.example.salesapp.data.remote.dto.AddCartItemRequestDto
import com.example.salesapp.data.remote.dto.CartDto
import com.example.salesapp.data.remote.dto.ErrorResponse
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartService: CartService,
    private val moshi: Moshi // Inject Moshi để parse lỗi
) {

    // Hàm này sẽ được gọi từ ProductDetailViewModel
    suspend fun addItemToCart(productId: Int, quantity: Int): Result<CartDto> {
        return try {
            val request = AddCartItemRequestDto(productId, quantity)
            val response = cartService.addItemToCart(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Parse lỗi (ví dụ: "Số lượng không hợp lệ", "Sản phẩm không tìm thấy")
                val errorMessage = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // Hàm helper parse lỗi (copy từ ProductRepository)
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