// Vị trí: .../data/remote/AuthInterceptor.kt
package com.example.salesapp.data.remote

import com.example.salesapp.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val prefsRepository: UserPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Lấy request gốc
        val originalRequest = chain.request()

        // Lấy token từ DataStore.
        // Dùng runBlocking vì Interceptor không phải là suspend function.
        // Đây là một trong số ít trường hợp runBlocking được chấp nhận.
        val token = runBlocking {
            prefsRepository.authToken.first() // Lấy giá trị token đầu tiên
        }

        // Nếu không có token (ví dụ: đang gọi API Login/Register),
        // thì cứ thực hiện request gốc
        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // Nếu có token, tạo request mới và gắn "Authorization" header
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        // Thực hiện request mới
        return chain.proceed(newRequest)
    }
}