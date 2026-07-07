// feature/watchlist/src/main/java/com/sypark/finnhub/feature/watchlist/WatchlistScreen.kt
package com.sypark.finnhub.feature.watchlist

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.ui.component.AppSnackbarHost
import com.sypark.finnhub.core.ui.component.CapsuleButton
import com.sypark.finnhub.core.ui.component.ConnectionBanner
import com.sypark.finnhub.core.ui.component.EmptyState
import com.sypark.finnhub.core.ui.component.ErrorBanner
import com.sypark.finnhub.core.ui.component.QuoteRow
import com.sypark.finnhub.core.ui.component.QuoteRowSkeleton
import com.sypark.finnhub.core.ui.model.ConnectionBannerState
import com.sypark.finnhub.core.ui.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest

private fun ConnectionStatus.toBannerState(): ConnectionBannerState = when (this) {
    ConnectionStatus.Connected -> ConnectionBannerState.LIVE
    ConnectionStatus.Connecting, ConnectionStatus.Reconnecting -> ConnectionBannerState.RECONNECTING
    ConnectionStatus.Disconnected -> ConnectionBannerState.DELAYED
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun CustomPullRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
) {
    val rotation = if (isRefreshing) {
        val transition = rememberInfiniteTransition(label = "pullRefreshSpin")
        val angle by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(animation = tween(durationMillis = 800, easing = LinearEasing)),
            label = "pullRefreshAngle",
        )
        angle
    } else {
        (state.distanceFraction * 360f).coerceIn(0f, 360f)
    }
    Box(
        modifier = modifier
            .size(32.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Filled.Refresh,
            contentDescription = null,
            tint = AppTheme.extended.live,
            modifier = Modifier.size(18.dp).rotate(rotation),
        )
    }
}

@Composable
fun WatchlistRoute(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WatchlistViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.onIntent(WatchlistIntent.Load) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is WatchlistEffect.NavigateToDetail -> onNavigateToDetail(effect.symbol)
                WatchlistEffect.NavigateToSearch -> onNavigateToSearch()
                is WatchlistEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    WatchlistScreen(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState, modifier = modifier)
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    state: WatchlistState,
    onIntent: (WatchlistIntent) -> Unit,
    snackbarHostState: androidx.compose.material3.SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ConnectionBanner(state = state.connectionStatus.toBannerState())

            when {
                state.isLoading -> LazyColumn { items(5) { QuoteRowSkeleton() } }
                state.error != null && state.items.isEmpty() -> ErrorBanner(
                    message = "네트워크 연결을 확인해 주세요",
                    onRetry = { onIntent(WatchlistIntent.Load) },
                )
                state.items.isEmpty() -> EmptyState(
                    title = "아직 관심종목이 없어요",
                    description = "종목이나 환율을 검색해 추가해 보세요",
                    ctaLabel = "종목 검색",
                    onCtaClick = { onIntent(WatchlistIntent.OpenSearch) },
                )
                else -> {
                    if (state.error != null) {
                        ErrorBanner(message = "네트워크 연결을 확인해 주세요", onRetry = { onIntent(WatchlistIntent.Load) })
                    }
                    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                    var draggingIndex by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Int?>(null) }
                    var dragOffsetY by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0f) }
                    val pullToRefreshState = rememberPullToRefreshState()
                    androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onIntent(WatchlistIntent.Refresh) },
                        state = pullToRefreshState,
                        indicator = {
                            CustomPullRefreshIndicator(
                                state = pullToRefreshState,
                                isRefreshing = state.isRefreshing,
                                modifier = Modifier.align(androidx.compose.ui.Alignment.TopCenter),
                            )
                        },
                    ) {
                        LazyColumn(state = listState) {
                            items(items = state.items, key = { it.symbol }) { item ->
                                val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        if (value == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart) {
                                            onIntent(WatchlistIntent.Remove(item.symbol))
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                )
                                androidx.compose.material3.SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,
                                    backgroundContent = {
                                        androidx.compose.foundation.layout.Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(com.sypark.finnhub.core.ui.theme.AppTheme.extended.loss),
                                            contentAlignment = androidx.compose.ui.Alignment.CenterEnd,
                                        ) {
                                            androidx.compose.material3.Icon(
                                                imageVector = androidx.compose.material.icons.Icons.Filled.Delete,
                                                contentDescription = "삭제",
                                                tint = androidx.compose.ui.graphics.Color.White,
                                                modifier = Modifier.padding(end = com.sypark.finnhub.core.ui.theme.Spacing.space4),
                                            )
                                        }
                                    },
                                ) {
                                    val quote = state.quotes[item.symbol]
                                    androidx.compose.foundation.layout.Row(
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                    ) {
                                        QuoteRow(
                                            modifier = Modifier.weight(1f),
                                            symbol = item.symbol,
                                            displayName = item.displayName,
                                            assetType = item.assetType,
                                            price = quote?.price ?: "—",
                                            changePercent = quote?.changePercent ?: "—",
                                            changeDirection = quote?.changeDirection ?: com.sypark.finnhub.core.ui.model.ChangeDirection.FLAT,
                                            quoteSource = quote?.quoteSource ?: com.sypark.finnhub.core.ui.model.UiQuoteSource.CACHE,
                                            onClick = { onIntent(WatchlistIntent.OpenDetail(item.symbol)) },
                                        )
                                        androidx.compose.material3.Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Filled.DragHandle,
                                            contentDescription = "순서 변경",
                                            modifier = Modifier.pointerInput(item.symbol) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = { draggingIndex = state.items.indexOf(item) },
                                                    onDragEnd = {
                                                        val from = draggingIndex
                                                        draggingIndex = null
                                                        dragOffsetY = 0f
                                                        if (from != null) {
                                                            val to = (from + (dragOffsetY / com.sypark.finnhub.core.ui.theme.Spacing.quoteRowHeight.value).toInt())
                                                                .coerceIn(0, state.items.lastIndex)
                                                            if (to != from) onIntent(WatchlistIntent.Reorder(from, to))
                                                        }
                                                    },
                                                    onDragCancel = { draggingIndex = null; dragOffsetY = 0f },
                                                    onDrag = { change, dragAmount -> change.consume(); dragOffsetY += dragAmount.y },
                                                )
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        CapsuleButton(
            text = "종목 검색",
            onClick = { onIntent(WatchlistIntent.OpenSearch) },
            icon = {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(com.sypark.finnhub.core.ui.theme.Spacing.space4),
        )
        AppSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter),
        )
    }
}
