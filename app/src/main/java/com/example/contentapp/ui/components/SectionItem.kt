package com.example.contentapp.ui.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contentapp.data.model.ContentItem
import com.example.contentapp.data.model.Section

@Composable
fun SectionItem(
    section: Section,
    modifier: Modifier = Modifier,
    onLoadMore: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        val section by remember(section) { mutableStateOf(section) }
        Text(
            text = section.title ?: "",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        when (section.type?.lowercase()) {
            "horizontal_scroll", "horizontal", "queue" -> {
                HorizontalScrollLayout(
                    items = section.items,
                    modifier = Modifier.fillMaxWidth(),
                    loadHomeSections = onLoadMore::invoke
                )
            }
            "two_column_grid", "grid_2", "2_lines_grid" -> {
                TwoColumnGridLayout(
                    items = section.items,
                    modifier = Modifier.fillMaxWidth(),
                    loadHomeSections = onLoadMore::invoke
                )
            }
            "square_grid", "grid_square", "big square", "square", "big_square" -> {
                SquareGridLayout(
                    items = section.items,
                    modifier = Modifier.fillMaxWidth(),
                    loadHomeSections = onLoadMore::invoke
                )
            }
            else -> {
                VerticalListLayout(
                    items = section.items,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun HorizontalScrollLayout(
    items: List<ContentItem>,
    modifier: Modifier = Modifier,
    loadHomeSections: () -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        itemsIndexed(items) { index, item ->
            ContentItemCard(
                item = item,
                modifier = Modifier.width(200.dp)
            )

            if (index == items.lastIndex - 1) loadHomeSections.invoke()
        }
    }
}

@Composable
private fun TwoColumnGridLayout(
    items: List<ContentItem>,
    modifier: Modifier = Modifier,
    loadHomeSections: () -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        modifier = modifier.height(400.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items) { index, item ->
            ContentItemCard(
                item = item,
                modifier = Modifier.width(300.dp)
            )

            if (index == items.lastIndex - 1) loadHomeSections.invoke()
        }
    }
}

@Composable
private fun SquareGridLayout(
    items: List<ContentItem>,
    modifier: Modifier = Modifier,
    loadHomeSections: () -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(3),
        modifier = modifier.height(300.dp).width(500.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) { index, item ->
            ContentItemCard(
                item = item,
                modifier = Modifier.aspectRatio(2f)
            )
            if (index == items.lastIndex - 1) loadHomeSections.invoke()
        }
    }
}

@Composable
private fun VerticalListLayout(
    items: List<ContentItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.take(1).forEach { item ->
            ContentItemCard(
                item = item,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

