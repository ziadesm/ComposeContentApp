package com.example.contentapp.ui.main
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.contentapp.R
import com.example.contentapp.data.model.Section
import com.example.contentapp.ui.components.ErrorMessage
import com.example.contentapp.ui.components.LoadingIndicator
import com.example.contentapp.ui.components.SectionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSearch: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.main_screen_title))
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_screen_title)
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
                uiState.homeSections != null -> {
                    MainContent(
                        sections = uiState.homeSections?.sections ?: emptyList(),
                        modifier = Modifier.fillMaxSize(),
                        onLoadMore = { viewModel.loadHomeSections() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    sections: List<Section>,
    modifier: Modifier = Modifier,
    onLoadMore: () -> Unit
) {
    if (sections.isEmpty()) return
    val sectionItems by remember(sections) { mutableStateOf(sections) }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        itemsIndexed(sectionItems) { index, section ->
            SectionItem(
                section = section,
                modifier = Modifier.fillMaxWidth(),
                onLoadMore = onLoadMore
            )
        }
    }
}

