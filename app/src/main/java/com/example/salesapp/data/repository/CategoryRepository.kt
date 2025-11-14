package com.example.salesapp.data.repository

import com.example.salesapp.data.remote.api.CategoryService
import com.example.salesapp.data.remote.dto.CategoryDto
import com.example.salesapp.data.remote.dto.ErrorResponse
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryService: CategoryService,
    private val moshi: Moshi
) {
    suspend fun getCategories(): Result<List<CategoryDto>> {
        return try {
            val response = categoryService.getCategories()
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