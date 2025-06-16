package com.example.contentapp.data.api

import com.example.contentapp.data.model.HomeSectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ContentApiService {
    @GET("home_sections")
    suspend fun getHomeSections(
        @Query("page") page: Int = 1
    ): HomeSectionsResponse
}