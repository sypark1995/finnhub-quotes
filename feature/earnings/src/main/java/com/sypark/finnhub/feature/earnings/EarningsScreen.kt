package com.sypark.finnhub.feature.earnings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sypark.finnhub.core.ui.component.EmptyState
import com.sypark.finnhub.core.ui.theme.AppTheme
import com.sypark.finnhub.core.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EarningsRoute(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EarningsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onIntent(EarningsIntent.Load) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is EarningsEffect.NavigateToDetail -> onNavigateToDetail(effect.symbol)
            }
        }
    }

    EarningsScreen(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}

@Composable
fun EarningsScreen(
    state: EarningsState,
    onIntent: (EarningsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            state.events.isEmpty() -> EmptyState(
                title = "다가오는 실적 발표가 없습니다",
                description = "관심종목을 추가하면 실적 발표일을 볼 수 있어요",
                ctaLabel = null,
                onCtaClick = null,
            )
            else -> LazyColumn {
                items(items = state.events, key = { "${it.symbol}_${it.dateText}" }) { event ->
                    EarningsEventRow(event = event, onClick = { onIntent(EarningsIntent.OpenDetail(event.symbol)) })
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline),
                    )
                }
            }
        }
    }
}

@Composable
private fun EarningsEventRow(event: EarningsEventUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.space4, vertical = Spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(horizontal = Spacing.space2, vertical = Spacing.space1),
        ) {
            Text(text = event.dateText, style = MaterialTheme.typography.labelLarge)
        }
        Column(modifier = Modifier.weight(1f).padding(start = Spacing.space3)) {
            Text(text = event.symbol, style = MaterialTheme.typography.titleMedium)
            Text(
                text = event.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            if (event.timingText.isNotEmpty()) {
                Text(
                    text = event.timingText,
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTheme.extended.neutral,
                )
            }
            Text(
                text = event.epsEstimateText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
