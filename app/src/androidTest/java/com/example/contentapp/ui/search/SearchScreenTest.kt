package com.example.contentapp.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.contentapp.data.model.Result
import com.example.contentapp.data.model.SearchResponse
import com.example.contentapp.data.model.SearchResult
import com.example.contentapp.data.repository.ContentRepository
import com.example.contentapp.ui.theme.ContentAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Mock
    private lateinit var repository: ContentRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        hiltRule.inject()
    }

    @Test
    fun searchScreen_displaysSearchPlaceholder() {
        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Start typing to search for content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search content...").assertIsDisplayed()
    }

    @Test
    fun searchScreen_backButtonNavigatesBack() {
        // Given
        var backNavigationCalled = false

        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = { backNavigationCalled = true }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backNavigationCalled)
    }

    @Test
    fun searchScreen_searchInputAcceptsText() {
        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = {}
                )
            }
        }

        // Then
        val searchField = composeTestRule.onNodeWithText("Search content...")
        searchField.performTextInput("test query")
        searchField.assertTextContains("test query")
    }

    @Test
    fun searchScreen_clearButtonClearsSearch() {
        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = {}
                )
            }
        }

        // Then
        val searchField = composeTestRule.onNodeWithText("Search content...")
        searchField.performTextInput("test query")
        
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        searchField.assertTextEquals("Search content...")
    }

    @Test
    fun searchScreen_displaysLoadingState() {
        // Given
        whenever(repository.searchContent("test")).thenReturn(
            flowOf(Result.Loading)
        )

        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Search content...").performTextInput("test")

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysSearchResults() {
        // Given
        val mockResults = listOf(
            SearchResult(
                id = "1",
                title = "Test Result 1",
                subtitle = "Test Subtitle 1",
                imageUrl = null,
                contentType = "book",
                description = "Test description",
                author = "Test Author",
                duration = null,
                episodeNumber = null,
                relevanceScore = 0.9f
            ),
            SearchResult(
                id = "2",
                title = "Test Result 2",
                subtitle = "Test Subtitle 2",
                imageUrl = null,
                contentType = "podcast",
                description = "Test description 2",
                author = "Test Host",
                duration = "30 min",
                episodeNumber = 5.0,
                relevanceScore = 0.8f
            )
        )
        val mockResponse = SearchResponse(
            results = mockResults,
            totalCount = 2,
            query = "test"
        )
        whenever(repository.searchContent("test")).thenReturn(
            flowOf(Result.Success(mockResponse))
        )

        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search content...").performTextInput("test")
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText("Found 2 results for \"test\"").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Result 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Result 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Author").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Host").assertIsDisplayed()
        composeTestRule.onNodeWithText("30 min").assertIsDisplayed()
        composeTestRule.onNodeWithText("BOOK").assertIsDisplayed()
        composeTestRule.onNodeWithText("PODCAST").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysErrorState() {
        // Given
        val errorMessage = "Search failed"
        whenever(repository.searchContent("test")).thenReturn(
            flowOf(Result.Error(Exception(errorMessage)))
        )

        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search content...").performTextInput("test")
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysNoResultsState() {
        // Given
        val mockResponse = SearchResponse(
            results = emptyList(),
            totalCount = 0,
            query = "nonexistent"
        )
        whenever(repository.searchContent("nonexistent")).thenReturn(
            flowOf(Result.Success(mockResponse))
        )

        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search content...").performTextInput("nonexistent")
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText("Found 0 results for \"nonexistent\"").assertIsDisplayed()
    }

    @Test
    fun searchScreen_retryButtonTriggersNewSearch() {
        // Given
        val errorMessage = "Network error"
        whenever(repository.searchContent("test")).thenReturn(
            flowOf(Result.Error(Exception(errorMessage)))
        )

        // When
        composeTestRule.setContent {
            ContentAppTheme {
                SearchScreen(
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search content...").performTextInput("test")
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
    }
}

