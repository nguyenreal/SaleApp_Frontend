// Vị trí: .../com/example/salesapp/data/remote/api/AuthService.kt
package com.example.salesapp.data.remote.api

import com.example.salesapp.data.remote.dto.AuthResponse
import com.example.salesapp.data.remote.dto.LoginDto
import com.example.salesapp.data.remote.dto.RegisterDto
import com.example.salesapp.data.remote.dto.RegisterResponse
// import com.example.salesapp.data.remote.dto.RegisterDto // Sẽ thêm sau
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    // Khớp với AuthController.cs
    @POST("api/Auth/login") // Dùng đường dẫn tương đối
    suspend fun login(@Body request: LoginDto): Response<AuthResponse>

    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterDto): Response<RegisterResponse>
}