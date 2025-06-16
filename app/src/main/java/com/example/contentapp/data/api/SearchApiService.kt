package com.example.contentapp.data.api
import com.example.contentapp.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface SearchApiService {
    @POST("{search}")
    suspend fun searchContent(
        @Path("search") query: String,
    ): Response<SearchResponse>
}

