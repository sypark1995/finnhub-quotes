// feature/search/src/main/java/com/sypark/finnhub/feature/search/SearchScreen.kt
package com.sypark.finnhub.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sypark.finnhub.core.ui.theme.ShapeCard
import com.sypark.finnhub.core.ui.theme.ShapePill
import com.sypark.finnhub.core.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchRoute(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SearchEffect.NavigateToDetail -> onNavigateToDetail(effect.symbol)
            }
        }
    }

    SearchScreen(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}

@Composable
fun SearchScreen(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.space4),
            placeholder = { Text("종목·환율 검색") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(onClick = { onIntent(SearchIntent.QueryChanged("")) }) {
                        Icon(Icons.Filled.Clear, contentDescription = "지우기")
                    }
                }
            },
            singleLine = true,
            shape = ShapeCard,
        )

        LazyRow(
            modifier = Modifier.padding(horizontal = Spacing.space4),
            horizontalArrangement = Arrangement.spacedBy(Spacing.space2),
        ) {
            items(AssetTypeFilter.entries) { filter ->
                FilterPill(
                    label = filter.label,
                    selected = filter == state.selectedFilter,
                    onClick = { onIntent(SearchIntent.FilterChanged(filter)) },
                )
            }
        }

        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = Spacing.space2))

        // Task 30 replaces this line with the result list + empty/no-result states.
        SearchResultsPlaceholder(state = state, onIntent = onIntent)
    }
}

@Composable
private fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = modifier
            .clip(ShapePill)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.space4, vertical = Spacing.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

@Composable
private fun SearchResultsPlaceholder(state: SearchState, onIntent: (SearchIntent) -> Unit) {
    // Intentionally minimal — Task 30 is the real implementation of this region.
}
