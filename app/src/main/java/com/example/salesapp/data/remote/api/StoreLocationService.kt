// Vị trí: .../api/StoreLocationService.kt
package com.example.salesapp.data.remote.api

import com.example.salesapp.data.remote.dto.StoreLocationDto
import retrofit2.Response
import retrofit2.http.GET

interface StoreLocationService {

    // GET /api/StoreLocations
    @GET("api/StoreLocations")
    suspend fun getStoreLocations(): Response<List<StoreLocationDto>>
}