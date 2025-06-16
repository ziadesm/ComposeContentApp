package com.example.contentapp.data.repository

import com.example.contentapp.data.model.HomeSectionsResponse
import com.example.contentapp.data.model.Result
import com.example.contentapp.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun getHomeSections(page: Int): Flow<HomeSectionsResponse>
    fun searchContent(query: String): Flow<Result<SearchResponse>>
}

