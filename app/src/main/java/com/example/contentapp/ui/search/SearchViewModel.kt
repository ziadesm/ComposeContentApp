package com.example.contentapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contentapp.data.model.Result
import com.example.contentapp.data.model.SearchResponse
import com.example.contentapp.data.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ContentRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        searchQuery
            .debounce(200)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query ->
                searchContent(query)
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _uiState.value = SearchUiState()
        }
    }

    private fun searchContent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchContent(query).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            searchResults = result.data,
                            error = null,
                            hasSearched = true
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Unknown error occurred",
                            hasSearched = true
                        )
                    }
                }
            }
        }
    }

    fun retry() {
        if (searchQuery.value.isNotBlank()) {
            searchContent(searchQuery.value)
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = SearchUiState()
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchResults: SearchResponse? = null,
    val error: String? = null,
    val hasSearched: Boolean = false
)

