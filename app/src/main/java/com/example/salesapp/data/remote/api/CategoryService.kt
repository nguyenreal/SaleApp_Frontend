package com.example.salesapp.data.remote.api

import com.example.salesapp.data.remote.dto.CategoryDto
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {

    @GET("api/Categories")
    suspend fun getCategories(): Response<List<CategoryDto>>
}