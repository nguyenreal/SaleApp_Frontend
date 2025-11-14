// Vị trí: .../api/CartService.kt
package com.example.salesapp.data.remote.api

import com.example.salesapp.data.remote.dto.AddCartItemRequestDto
import com.example.salesapp.data.remote.dto.CartDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CartService {

    // POST /api/Cart/items
    // Khớp với hàm AddItem trong CartController.cs
    @POST("api/Cart/items")
    suspend fun addItemToCart(
        @Body request: AddCartItemRequestDto
    ): Response<CartDto> // Trả về giỏ hàng đã cập nhật
}