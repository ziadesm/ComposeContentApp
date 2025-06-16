@file:Suppress("KotlinConstantConditions")
package com.example.contentapp.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.contentapp.R
import com.example.contentapp.data.model.ContentItem
import com.example.contentapp.data.model.SearchResult
import com.example.contentapp.ui.components.ContentItemCard
import com.example.contentapp.ui.components.ErrorMessage
import com.example.contentapp.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::updateSearchQuery,
                        onClearQuery = viewModel::clearSearch,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error,
                        onRetry = { viewModel.retry() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.searchResults != null -> {
                    SearchResults(
                        searchResults = uiState.searchResults?.results ?: emptyList(),
                        query = uiState.searchResults?.query,
                        totalCount = uiState.searchResults?.totalCount ?: 0,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.hasSearched && uiState.searchResults?.results?.isEmpty() == true -> {
                    EmptySearchResults(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    SearchPlaceholder(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text(text = stringResource(R.string.search_hint))
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true
    )
}

@Composable
private fun SearchResults(
    searchResults: List<SearchResult>,
    query: String?,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Found $totalCount results for \"$query\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(searchResults) { result ->
            ContentItemCard(
                item = result.toContentItem(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EmptySearchResults(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_results),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Start typing to search for content",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun SearchResult.toContentItem(): ContentItem {
    return ContentItem(
        id = this.id,
        title = this.title,
        subtitle = this.subtitle,
        imageUrl = this.imageUrl,
        contentType = this.contentType,
        description = this.description,
        author = this.author,
        duration = this.duration,
        episodeNumber = this.episodeNumber
    )
}

