// Vị trí: .../api/CartService.kt
package com.example.salesapp.data.remote.api

import com.example.salesapp.data.remote.dto.AddCartItemRequestDto
import com.example.salesapp.data.remote.dto.CartDto
import com.example.salesapp.data.remote.dto.UpdateCartItemRequestDto // <-- Thêm
import retrofit2.Response
import retrofit2.http.* // <-- Thêm

interface CartService {

    // (Hàm cũ)
    @POST("api/Cart/items")
    suspend fun addItemToCart(
        @Body request: AddCartItemRequestDto
    ): Response<CartDto>

    // --- THÊM CÁC HÀM MỚI ---

    // GET /api/Cart/my-cart
    @GET("api/Cart/my-cart")
    suspend fun getMyCart(): Response<CartDto>

    // PUT /api/Cart/items/{cartItemId}
    @PUT("api/Cart/items/{cartItemId}")
    suspend fun updateItemQuantity(
        @Path("cartItemId") cartItemId: Int,
        @Body request: UpdateCartItemRequestDto
    ): Response<CartDto>

    // DELETE /api/Cart/items/{cartItemId}
    @DELETE("api/Cart/items/{cartItemId}")
    suspend fun removeItemFromCart(
        @Path("cartItemId") cartItemId: Int
    ): Response<CartDto>
}