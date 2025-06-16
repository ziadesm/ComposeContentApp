package com.example.contentapp.data.repository
import com.example.contentapp.data.api.ContentApiService
import com.example.contentapp.data.api.SearchApiService
import com.example.contentapp.data.model.HomeSectionsResponse
import com.example.contentapp.data.model.Result
import com.example.contentapp.data.model.SearchResponse
import com.example.contentapp.data.model.Section
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ContentRepositoryImplTest {

    @Mock
    private lateinit var contentApiService: ContentApiService

    @Mock
    private lateinit var searchApiService: SearchApiService

    private lateinit var repository: ContentRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ContentRepositoryImpl(contentApiService, searchApiService)
    }

    @Test
    fun `getHomeSections should emit success`() = runTest {
        // Given
        val mockSections = listOf(
            Section(
                id = "1",
                title = "Test Section",
                type = "books",
                layout = "grid",
                items = emptyList()
            )
        )
        val mockResponse = HomeSectionsResponse(sections = mockSections)
        whenever(contentApiService.getHomeSections()).thenReturn(
            mockResponse
        )

        // When
        val results = repository.getHomeSections(1).toList()

        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is HomeSectionsResponse)
        assertEquals(mockResponse, (results[0] as HomeSectionsResponse))
    }

    @Test
    fun `searchContent should emit loading then success`() = runTest {
        // Given
        val query = "test"
        val mockResponse = SearchResponse(
            results = emptyList(),
            totalCount = 0,
            query = query
        )
        whenever(searchApiService.searchContent(query)).thenReturn(
            Response.success(mockResponse)
        )

        // When
        val results = repository.searchContent(query).toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertEquals(mockResponse, (results[1] as Result.Success).data)
    }

    @Test
    fun `searchContent should emit loading then error on HTTP error`() = runTest {
        // Given
        val query = "test"
        whenever(searchApiService.searchContent(query)).thenReturn(
            Response.error(500, "Internal Server Error".toResponseBody())
        )

        // When
        val results = repository.searchContent(query).toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        val error = (results[1] as Result.Error).exception
        assertTrue(error.message!!.contains("HTTP 500"))
    }

    @Test
    fun `searchContent should emit loading then error on unexpected exception`() = runTest {
        // Given
        val query = "test"
        whenever(searchApiService.searchContent(query)).thenThrow(
            RuntimeException("Unexpected error")
        )

        // When
        val results = repository.searchContent(query).toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        val error = (results[1] as Result.Error).exception
        assertTrue(error.message!!.contains("Unexpected error"))
    }
}

