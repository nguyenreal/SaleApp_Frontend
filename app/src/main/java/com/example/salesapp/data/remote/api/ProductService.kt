// Vị trí: .../data/remote/api/ProductService.kt
package com.example.salesapp.data.remote.api

import com.example.salesapp.data.remote.dto.ProductDetailDto
import com.example.salesapp.data.remote.dto.ProductListItemDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    // GET /api/Products
    @GET("api/Products")
    suspend fun getProducts(
        @Query("categoryId") categoryId: Int? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("sortBy") sortBy: String? = null
    ): Response<List<ProductListItemDto>> // API trả về một List

    // GET /api/Products/{id}
    @GET("api/Products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDetailDto>
}