// Vị trí: .../repository/MapRepository.kt
package com.example.salesapp.data.repository

import com.example.salesapp.data.remote.api.StoreLocationService
import com.example.salesapp.data.remote.dto.StoreLocationDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepository @Inject constructor(
    private val locationService: StoreLocationService
) {
    suspend fun getStoreLocations(): Result<List<StoreLocationDto>> {
        return try {
            val response = locationService.getStoreLocations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Tải địa điểm thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
}