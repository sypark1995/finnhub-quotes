// feature/detail/src/main/java/com/sypark/finnhub/feature/detail/DetailScreen.kt
package com.sypark.finnhub.feature.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sypark.finnhub.core.ui.component.PriceChangeBadge
import com.sypark.finnhub.core.ui.component.QuoteSourceIndicator
import com.sypark.finnhub.core.ui.theme.PriceTypographyLarge
import com.sypark.finnhub.core.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailRoute(
    onNavigateBack: () -> Unit,
    onNavigateToAlertCreate: (String) -> Unit,
    onNavigateToPeerDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onIntent(DetailIntent.Load) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is DetailEffect.NavigateToAlertCreate -> onNavigateToAlertCreate(effect.symbol)
                is DetailEffect.ShowSnackbar -> Unit
            }
        }
    }

    DetailScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToPeerDetail = onNavigateToPeerDetail,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailState,
    onIntent: (DetailIntent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPeerDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(state.symbol) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로") }
                },
                actions = {
                    IconButton(onClick = { onIntent(DetailIntent.ToggleWatchlist) }) {
                        Icon(
                            imageVector = if (state.isInWatchlist) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = if (state.isInWatchlist) "관심종목에서 제거" else "관심종목에 추가",
                        )
                    }
                    IconButton(onClick = { onIntent(DetailIntent.CreateAlert) }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "알림 생성")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            state.quote?.let { quote ->
                Column(modifier = Modifier.padding(Spacing.space4)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(text = quote.price, style = PriceTypographyLarge)
                        androidx.compose.foundation.layout.Spacer(Modifier.padding(start = Spacing.space2))
                        QuoteSourceIndicator(source = quote.quoteSource)
                    }
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(text = "${quote.change} (${quote.changePercent})", style = MaterialTheme.typography.bodyMedium)
                        androidx.compose.foundation.layout.Spacer(Modifier.padding(start = Spacing.space2))
                        PriceChangeBadge(changePercent = quote.changePercent.removeSuffix("%").toDoubleOrNull() ?: 0.0)
                    }
                    Text(
                        text = "고 ${quote.high}  저 ${quote.low}  시 ${quote.open}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            ScrollableTabRow(selectedTabIndex = DetailTab.entries.indexOf(state.selectedTab)) {
                DetailTab.entries.forEach { tab ->
                    Tab(
                        selected = tab == state.selectedTab,
                        onClick = { onIntent(DetailIntent.SelectTab(tab)) },
                        text = { Text(tabLabel(tab)) },
                    )
                }
            }

            Crossfade(targetState = state.selectedTab, label = "detailTab") { tab ->
                when (tab) {
                    DetailTab.CHART -> ChartTab(state = state, onIntent = onIntent)
                    DetailTab.PROFILE -> ProfileTab(state = state)
                    DetailTab.NEWS -> NewsTab(state = state)
                    DetailTab.PEERS -> PeersTab(state = state, onNavigateToPeerDetail = onNavigateToPeerDetail)
                }
            }
        }
    }
}

private fun tabLabel(tab: DetailTab): String = when (tab) {
    DetailTab.CHART -> "차트"
    DetailTab.PROFILE -> "프로필"
    DetailTab.NEWS -> "뉴스"
    DetailTab.PEERS -> "Peers"
}

// Minimal stubs so this task compiles standalone — Tasks 47–50 each *modify* this file,
// replacing exactly one of these four with its real implementation.
@Composable
private fun ChartTab(state: DetailState, onIntent: (DetailIntent) -> Unit) {
    Text(text = "차트 (준비 중)", modifier = Modifier.padding(Spacing.space4))
}

@Composable
private fun ProfileTab(state: DetailState) {
    Text(text = "프로필 (준비 중)", modifier = Modifier.padding(Spacing.space4))
}

@Composable
private fun NewsTab(state: DetailState) {
    Text(text = "뉴스 (준비 중)", modifier = Modifier.padding(Spacing.space4))
}

@Composable
private fun PeersTab(state: DetailState, onNavigateToPeerDetail: (String) -> Unit) {
    Text(text = "Peers (준비 중)", modifier = Modifier.padding(Spacing.space4))
}
