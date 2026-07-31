package com.sypark.finnhub.feature.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sypark.finnhub.core.ui.component.AlertCard
import com.sypark.finnhub.core.ui.component.CapsuleButton
import com.sypark.finnhub.core.ui.component.EmptyState
import com.sypark.finnhub.core.ui.theme.AppTheme
import com.sypark.finnhub.core.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AlertListRoute(
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlertListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onIntent(AlertListIntent.Load) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AlertListEffect.NavigateToSearch -> onNavigateToSearch()
            }
        }
    }

    AlertListScreen(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}

@Composable
fun AlertListScreen(
    state: AlertListState,
    onIntent: (AlertListIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.alerts.isEmpty() && !state.isLoading) {
            EmptyState(
                title = "설정된 알림이 없습니다",
                description = "관심종목에서 알림을 추가해 보세요",
                ctaLabel = null,
                onCtaClick = null,
            )
        } else {
            LazyColumn {
                items(items = state.alerts, key = { it.id }) { alert ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                onIntent(AlertListIntent.Delete(alert.id))
                                true
                            } else {
                                false
                            }
                        },
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AppTheme.extended.loss),
                                contentAlignment = Alignment.CenterEnd,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "삭제",
                                    tint = Color.White,
                                    modifier = Modifier.padding(end = Spacing.space4),
                                )
                            }
                        },
                    ) {
                        AlertCard(
                            symbol = alert.symbol,
                            conditionText = alert.conditionText,
                            isEnabled = alert.isEnabled,
                            triggeredText = alert.triggeredText,
                            onToggleEnabled = { enabled -> onIntent(AlertListIntent.ToggleEnabled(alert.id, enabled)) },
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline),
                    )
                }
            }
        }

        CapsuleButton(
            text = "알림 추가",
            onClick = { onIntent(AlertListIntent.OpenCreate) },
            icon = { Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.space4),
        )
    }
}
