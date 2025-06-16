package com.example.contentapp.ui.main
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contentapp.data.model.HomeSectionsResponse
import com.example.contentapp.data.model.Section
import com.example.contentapp.data.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var allItems = mutableListOf<Section>()

    init {
        loadHomeSections()
    }

    private fun checkReachingLastPage() =
        _uiState.value.homeSections?.pagination?.totalPages?.let { currentPage >= it } ?: false

    fun loadHomeSections() {
        if (checkReachingLastPage()) return
        loadContentHomeSections()
    }

    fun loadContentHomeSections() = repository.getHomeSections(currentPage)
        .onStart { if (currentPage == 1) _uiState.emit(_uiState.value.copy(isLoading = true, error = null)) }
        .onEach { currentPage++ }
        .map { mergeNewContent(it) }
        .onEach { _uiState.emit(_uiState.value.copy(isLoading = false, homeSections = it, error = null)) }
        .catch { _uiState.emit(_uiState.value.copy(isLoading = false, error = it.message)) }
        .flowOn(Dispatchers.IO)
        .launchIn(viewModelScope)

    private fun mergeNewContent(newSections: HomeSectionsResponse): HomeSectionsResponse {
        if (allItems.isEmpty()) {
            allItems.addAll(newSections.sections)
            return HomeSectionsResponse(newSections.sections)
        }

        val current = allItems.toMutableList()

        val merged = current.map { existing ->
            val matchingNew = newSections.sections.find { it.order == existing.order }

            if (matchingNew != null) {
                existing.copy(
                    items = existing.items + matchingNew.items
                )
            } else existing
        }
        allItems.clear()
        allItems.addAll(merged)
        return HomeSectionsResponse(allItems.toList(), newSections.pagination)
    }

    fun retry() {
        currentPage = 1
        loadHomeSections()
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val homeSections: HomeSectionsResponse? = null,
    val error: String? = null
)

