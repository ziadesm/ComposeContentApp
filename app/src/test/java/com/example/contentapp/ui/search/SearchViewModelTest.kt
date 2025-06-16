package com.example.contentapp.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.contentapp.data.model.Result
import com.example.contentapp.data.model.SearchResponse
import com.example.contentapp.data.model.SearchResult
import com.example.contentapp.data.repository.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: ContentRepository

    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateSearchQuery should update search query state`() = runTest {
        // Given
        viewModel = SearchViewModel(repository)
        val query = "test query"

        // When
        viewModel.updateSearchQuery(query)

        // Then
        assertEquals(query, viewModel.searchQuery.value)
    }

    @Test
    fun `updateSearchQuery with blank query should reset ui state`() = runTest {
        // Given
        viewModel = SearchViewModel(repository)

        // When
        viewModel.updateSearchQuery("")

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.searchResults)
        assertNull(uiState.error)
        assertFalse(uiState.hasSearched)
    }

    @Test
    fun `debounced search should trigger after 200ms delay`() = runTest {
        // Given
        val query = "test"
        val mockResults = listOf(
            SearchResult(
                id = "1",
                title = "Test Result",
                subtitle = null,
                imageUrl = null,
                contentType = "book",
                description = null,
                author = null,
                duration = null,
                episodeNumber = null,
                relevanceScore = null
            )
        )
        val mockResponse = SearchResponse(
            results = mockResults,
            totalCount = 1,
            query = query
        )
        whenever(repository.searchContent(query)).thenReturn(
            flowOf(Result.Success(mockResponse))
        )

        viewModel = SearchViewModel(repository)

        // When
        viewModel.updateSearchQuery(query)
        
        testScheduler.advanceTimeBy(100)
        verify(repository, never()).searchContent(query)

        testScheduler.advanceTimeBy(100)
        testScheduler.runCurrent()

        // Then
        verify(repository, times(1)).searchContent(query)
        val finalState = viewModel.uiState.value
        assertEquals(mockResponse, finalState.searchResults)
        assertTrue(finalState.hasSearched)
    }

    @Test
    fun `multiple rapid queries should only trigger search for last query`() = runTest {
        // Given
        val query1 = "test1"
        val query2 = "test2"
        val query3 = "test3"
        
        val mockResponse = SearchResponse(
            results = emptyList(),
            totalCount = 0,
            query = query3
        )
        whenever(repository.searchContent(query3)).thenReturn(
            flowOf(Result.Success(mockResponse))
        )

        viewModel = SearchViewModel(repository)

        viewModel.updateSearchQuery(query1)
        testScheduler.advanceTimeBy(50)
        
        viewModel.updateSearchQuery(query2)
        testScheduler.advanceTimeBy(50)
        
        viewModel.updateSearchQuery(query3)
        testScheduler.advanceTimeBy(200)
        testScheduler.runCurrent()

        verify(repository, never()).searchContent(query1)
        verify(repository, never()).searchContent(query2)
        verify(repository, times(1)).searchContent(query3)
    }

    @Test
    fun `search should handle error state correctly`() = runTest {
        // Given
        val query = "test"
        val errorMessage = "Network error"
        whenever(repository.searchContent(query)).thenReturn(
            flowOf(Result.Error(Exception(errorMessage)))
        )

        viewModel = SearchViewModel(repository)

        // When
        viewModel.updateSearchQuery(query)
        testScheduler.advanceTimeBy(200)
        testScheduler.runCurrent()

        // Then
        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertNull(finalState.searchResults)
        assertEquals(errorMessage, finalState.error)
        assertTrue(finalState.hasSearched)
    }

    @Test
    fun `clearSearch should reset query and ui state`() = runTest {
        // Given
        viewModel = SearchViewModel(repository)
        viewModel.updateSearchQuery("test")

        // When
        viewModel.clearSearch()

        // Then
        assertEquals("", viewModel.searchQuery.value)
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.searchResults)
        assertNull(uiState.error)
        assertFalse(uiState.hasSearched)
    }

    @Test
    fun `retry should trigger search with current query`() = runTest {
        // Given
        val query = "test"
        val mockResponse = SearchResponse(
            results = emptyList(),
            totalCount = 0,
            query = query
        )
        whenever(repository.searchContent(query)).thenReturn(
            flowOf(Result.Success(mockResponse))
        )

        viewModel = SearchViewModel(repository)
        viewModel.updateSearchQuery(query)
        testScheduler.advanceTimeBy(200)
        testScheduler.runCurrent()

        // When
        viewModel.retry()

        // Then
        verify(repository, times(1)).searchContent(query)
    }
}

