// feature/detail/src/main/java/com/sypark/finnhub/feature/detail/DetailScreen.kt
package com.sypark.finnhub.feature.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
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
                    DetailTab.PROFILE -> ProfileTab(state = state)
                    DetailTab.NEWS -> NewsTab(state = state)
                    DetailTab.PEERS -> PeersTab(state = state, onNavigateToPeerDetail = onNavigateToPeerDetail)
                }
            }
        }
    }
}

private fun tabLabel(tab: DetailTab): String = when (tab) {
    DetailTab.PROFILE -> "프로필"
    DetailTab.NEWS -> "뉴스"
    DetailTab.PEERS -> "Peers"
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
            .clip(com.sypark.finnhub.core.ui.theme.ShapePill)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.space4, vertical = Spacing.space2),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

@Composable
private fun ProfileTab(state: DetailState) {
    val profile = state.profile
    val metrics = state.metrics
    Column(modifier = Modifier.padding(Spacing.space4)) {
        if (profile != null) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(text = profile.name, style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = "${profile.industry} · ${profile.exchange}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = Spacing.space4))
            com.sypark.finnhub.core.ui.component.KeyValueRow(label = "시가총액", value = profile.marketCapText)
        }
        if (metrics != null) {
            com.sypark.finnhub.core.ui.component.KeyValueRow(label = "PER", value = metrics.peRatioText)
            com.sypark.finnhub.core.ui.component.KeyValueRow(label = "52주 최고", value = metrics.week52HighText)
            com.sypark.finnhub.core.ui.component.KeyValueRow(label = "52주 최저", value = metrics.week52LowText, showDivider = false)
        }
        if (profile == null && metrics == null) {
            Text(text = "프로필 정보를 불러올 수 없습니다", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun NewsTab(state: DetailState) {
    val context = androidx.compose.ui.platform.LocalContext.current
    if (state.news.isEmpty()) {
        com.sypark.finnhub.core.ui.component.EmptyState(
            title = "관련 뉴스가 없습니다",
            description = "최근 발행된 기사가 없어요",
            ctaLabel = null,
            onCtaClick = null,
        )
        return
    }
    androidx.compose.foundation.lazy.LazyColumn {
        items(items = state.news, key = { it.url }) { news ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        androidx.browser.customtabs.CustomTabsIntent.Builder().build().launchUrl(context, android.net.Uri.parse(news.url))
                    }
                    .padding(horizontal = Spacing.space4, vertical = Spacing.space3),
            ) {
                Text(
                    text = news.headline,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                Text(
                    text = "${news.source} · ${relativeTime(news.datetime)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outline),
            )
        }
    }
}

private fun relativeTime(epochMillis: Long): String {
    val diffMinutes = (System.currentTimeMillis() - epochMillis) / 60_000
    return when {
        diffMinutes < 60 -> "${diffMinutes}분 전"
        diffMinutes < 1_440 -> "${diffMinutes / 60}시간 전"
        else -> "${diffMinutes / 1_440}일 전"
    }
}

@Composable
private fun PeersTab(state: DetailState, onNavigateToPeerDetail: (String) -> Unit) {
    if (state.peers.isEmpty()) {
        com.sypark.finnhub.core.ui.component.EmptyState(
            title = "동종 업종 정보가 없습니다",
            description = "",
            ctaLabel = null,
            onCtaClick = null,
        )
        return
    }
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.padding(Spacing.space4),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.space2),
    ) {
        items(items = state.peers, key = { it }) { peer ->
            FilterPill(
                label = peer,
                selected = false,
                onClick = { onNavigateToPeerDetail(peer) },
            )
        }
    }
}
