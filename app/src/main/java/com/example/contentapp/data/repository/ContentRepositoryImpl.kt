package com.example.contentapp.data.repository
import com.example.contentapp.data.api.ContentApiService
import com.example.contentapp.data.api.SearchApiService
import com.example.contentapp.data.model.HomeSectionsResponse
import com.example.contentapp.data.model.Result
import com.example.contentapp.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl
@Inject
constructor(
    private val contentApiService: ContentApiService,
    private val searchApiService: SearchApiService,
): ContentRepository {

    override fun getHomeSections(page: Int): Flow<HomeSectionsResponse> = flow {
        emit(contentApiService.getHomeSections(page))
    }

    override fun searchContent(query: String): Flow<Result<SearchResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = searchApiService.searchContent(query)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    emit(Result.Success(data))
                } ?: emit(Result.Error(Exception("Empty response body")))
            } else {
                emit(Result.Error(Exception("HTTP ${response.code()}: ${response.message()}")))
            }
        } catch (e: IOException) {
            emit(Result.Error(Exception("Network error: ${e.message}", e)))
        } catch (e: Exception) {
            emit(Result.Error(Exception("Unexpected error: ${e.message}", e)))
        }
    }
}

