package com.sypark.finnhub.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.usecase.detail.GetCompanyNewsUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetPeersUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetQuoteUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetStockMetricsUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetStockProfileUseCase
import com.sypark.finnhub.core.domain.usecase.detail.IsInWatchlistUseCase
import com.sypark.finnhub.core.domain.usecase.detail.ToggleWatchlistUseCase
import com.sypark.finnhub.core.ui.model.UiQuoteSource
import com.sypark.finnhub.core.ui.util.changeDirectionOf
import com.sypark.finnhub.core.ui.util.formatLargeNumber
import com.sypark.finnhub.core.ui.util.formatPercent
import com.sypark.finnhub.core.ui.util.formatPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getQuoteUseCase: GetQuoteUseCase,
    private val observeQuotesUseCase: com.sypark.finnhub.core.domain.usecase.watchlist.ObserveQuotesUseCase,
    private val getStockProfileUseCase: GetStockProfileUseCase,
    private val getStockMetricsUseCase: GetStockMetricsUseCase,
    private val getPeersUseCase: GetPeersUseCase,
    private val getCompanyNewsUseCase: GetCompanyNewsUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase,
    private val isInWatchlistUseCase: IsInWatchlistUseCase,
) : ViewModel() {

    private val symbol: String = savedStateHandle.get<String>("symbol").orEmpty()
    private val assetType: AssetType = savedStateHandle.get<String>("assetTypeName")
        ?.let { runCatching { AssetType.valueOf(it) }.getOrNull() }
        ?: assetTypeFromSymbol(symbol)

    private val _state = MutableStateFlow(DetailState(symbol = symbol))
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DetailEffect>(extraBufferCapacity = 8, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val effect: SharedFlow<DetailEffect> = _effect.asSharedFlow()

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            DetailIntent.Load -> { load(); observeLiveQuote() }
            is DetailIntent.SelectTab -> _state.value = _state.value.copy(selectedTab = intent.tab)
            DetailIntent.ToggleWatchlist -> toggleWatchlist()
            DetailIntent.CreateAlert -> _effect.tryEmit(DetailEffect.NavigateToAlertCreate(symbol))
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val to = System.currentTimeMillis() / 1000
            val from = to - TimeUnit.DAYS.toSeconds(90)

            val quoteDeferred = async { getQuoteUseCase(symbol) }
            val profileDeferred = async { getStockProfileUseCase(symbol) }
            val metricsDeferred = async { getStockMetricsUseCase(symbol) }
            val peersDeferred = async { getPeersUseCase(symbol) }
            val newsDeferred = async {
                getCompanyNewsUseCase(symbol, isoDate(from * 1000), isoDate(to * 1000))
            }
            val isInWatchlistDeferred = async { isInWatchlistUseCase(symbol) }
            awaitAll(quoteDeferred, profileDeferred, metricsDeferred, peersDeferred, newsDeferred, isInWatchlistDeferred)

            val quoteResult = quoteDeferred.await()

            _state.value = _state.value.copy(
                isLoading = false,
                quote = (quoteResult as? AppResult.Success)?.data?.toQuoteUi(assetType),
                profile = (profileDeferred.await() as? AppResult.Success)?.data?.let {
                    // Finnhub returns marketCapitalization already expressed in millions of
                    // dollars (confirmed via direct API call: AAPL's value ~4,894,708 means
                    // $4.89T, not $4.89M) -- formatLargeNumber expects a raw dollar figure, so
                    // scale up first. Without this, every market cap displayed 1,000,000x too
                    // small (AAPL showed "$4.9M" instead of "$4.9T"; smaller caps like DELL's
                    // ~$253.7B showed as a bare unsuffixed "$253724.2").
                    val marketCapDollars = it.marketCapitalization * 1_000_000
                    StockProfileUi(it.name, it.exchange, it.industry, it.logoUrl, "$${formatLargeNumber(marketCapDollars)}", it.webUrl)
                },
                metrics = (metricsDeferred.await() as? AppResult.Success)?.data?.let {
                    StockMetricsUi(
                        peRatioText = it.peRatio?.toString() ?: "—",
                        week52HighText = it.week52High?.let { h -> formatPrice(h, assetType) } ?: "—",
                        week52LowText = it.week52Low?.let { l -> formatPrice(l, assetType) } ?: "—",
                    )
                },
                peers = (peersDeferred.await() as? AppResult.Success)?.data ?: emptyList(),
                news = (newsDeferred.await() as? AppResult.Success)?.data?.map { NewsUi(it.headline, it.source, it.url, it.imageUrl, it.datetime) } ?: emptyList(),
                isInWatchlist = isInWatchlistDeferred.await(),
                error = (quoteResult as? AppResult.Error)?.error,
            )
        }
    }

    private fun toggleWatchlist() {
        viewModelScope.launch {
            val profile = _state.value.profile
            val item = WatchlistItem(
                symbol = symbol,
                displayName = profile?.name ?: symbol,
                assetType = assetType,
                sortOrder = 0,
            )
            when (toggleWatchlistUseCase(item)) {
                is AppResult.Success -> _state.value = _state.value.copy(isInWatchlist = !_state.value.isInWatchlist)
                is AppResult.Error -> _effect.tryEmit(DetailEffect.ShowSnackbar("관심종목 변경에 실패했습니다"))
            }
        }
    }

    private fun observeLiveQuote() {
        viewModelScope.launch {
            observeQuotesUseCase(setOf(symbol)).collect { quotesBySymbol ->
                val quote = quotesBySymbol[symbol] ?: return@collect
                _state.value = _state.value.copy(quote = quote.toQuoteUi(assetType))
            }
        }
    }

    private fun isoDate(epochMillis: Long): String =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(epochMillis))

    private fun com.sypark.finnhub.core.domain.model.Quote.toQuoteUi(assetType: AssetType) = QuoteUi(
        price = formatPrice(price, assetType),
        change = formatPrice(change, assetType),
        changePercent = formatPercent(changePercent),
        changeDirection = changeDirectionOf(changePercent),
        high = formatPrice(high, assetType),
        low = formatPrice(low, assetType),
        open = formatPrice(open, assetType),
        quoteSource = when (source) {
            com.sypark.finnhub.core.domain.model.QuoteSource.WEBSOCKET -> UiQuoteSource.WEBSOCKET
            com.sypark.finnhub.core.domain.model.QuoteSource.REST -> UiQuoteSource.REST
            com.sypark.finnhub.core.domain.model.QuoteSource.CACHE -> UiQuoteSource.CACHE
        },
    )
}

private fun assetTypeFromSymbol(symbol: String): AssetType =
    if (symbol.contains(":") && symbol.contains("_")) {
        AssetType.FOREX
    } else {
        AssetType.STOCK
    }
