package com.example.salesapp.data.repository

import com.example.salesapp.data.remote.api.ProductService
import com.example.salesapp.data.remote.dto.ErrorResponse
import com.example.salesapp.data.remote.dto.ProductDetailDto
import com.example.salesapp.data.remote.dto.ProductListItemDto
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productService: ProductService,
    private val moshi: Moshi
) {
    /**
     * Lấy danh sách sản phẩm với các tham số filter và sort
     *
     * @param categoryId Lọc theo category (null = tất cả)
     * @param minPrice Giá tối thiểu
     * @param maxPrice Giá tối đa
     * @param sortBy Sắp xếp: "price_asc", "price_desc", "name_asc", "name_desc"
     */
    suspend fun getProducts(
        categoryId: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        sortBy: String? = null
    ): Result<List<ProductListItemDto>> {
        return try {
            val response = productService.getProducts(
                categoryId = categoryId,
                minPrice = minPrice,
                maxPrice = maxPrice,
                sortBy = sortBy
            )

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