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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sypark.finnhub.core.ui.component.EmptyState
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

        SearchResults(state = state, onIntent = onIntent)
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
private fun SearchResults(state: SearchState, onIntent: (SearchIntent) -> Unit) {
    when {
        state.query.isBlank() -> EmptyState(
            title = "종목·환율을 검색해 보세요",
            description = "심볼이나 회사명을 입력하면 결과가 표시됩니다",
            ctaLabel = null,
            onCtaClick = null,
        )
        state.isSearching -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        state.results.isEmpty() -> EmptyState(
            title = "검색 결과가 없습니다",
            description = "다른 검색어로 다시 시도해 보세요",
            ctaLabel = null,
            onCtaClick = null,
        )
        else -> LazyColumn {
            items(items = state.results, key = { it.symbol }) { result ->
                SearchResultRow(
                    result = result,
                    onToggleWatchlist = {
                        if (result.isInWatchlist) {
                            onIntent(SearchIntent.RemoveFromWatchlist(result.symbol))
                        } else {
                            onIntent(SearchIntent.AddToWatchlist(result))
                        }
                    },
                    onClick = { onIntent(SearchIntent.OpenDetail(result.symbol)) },
                )
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    result: SearchResultUi,
    onToggleWatchlist: () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.space4, vertical = Spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = result.symbol, style = MaterialTheme.typography.titleMedium)
            Text(
                text = result.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onToggleWatchlist) {
            Icon(
                imageVector = if (result.isInWatchlist) Icons.Filled.Star else Icons.Filled.Add,
                contentDescription = if (result.isInWatchlist) "관심종목에서 제거" else "관심종목에 추가",
                tint = if (result.isInWatchlist) MaterialTheme.colorScheme.primary else LocalContentColor.current,
            )
        }
    }
}
