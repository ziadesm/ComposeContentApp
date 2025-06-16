package com.example.contentapp.ui.main
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.contentapp.data.model.HomeSectionsResponse
import com.example.contentapp.data.model.Section
import com.example.contentapp.data.repository.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: ContentRepository

    private lateinit var viewModel: MainViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

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
    fun `loadHomeSections should update state to success`() = runTest {
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
        whenever(repository.getHomeSections(1)).thenReturn(
            flowOf(mockResponse)
        )

        // When
        viewModel = MainViewModel(repository)

        // Then
        val finalState = viewModel.uiState.value
        assertEquals(mockResponse, finalState.homeSections)
//        assertNull(finalState.error)
    }
}

