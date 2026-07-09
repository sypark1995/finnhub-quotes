package com.sypark.finnhub.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.usecase.detail.GetCandlesUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetCompanyNewsUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetPeersUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetQuoteUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetStockMetricsUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetStockProfileUseCase
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
    private val getStockProfileUseCase: GetStockProfileUseCase,
    private val getStockMetricsUseCase: GetStockMetricsUseCase,
    private val getPeersUseCase: GetPeersUseCase,
    private val getCandlesUseCase: GetCandlesUseCase,
    private val getCompanyNewsUseCase: GetCompanyNewsUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase,
) : ViewModel() {

    private val symbol: String = savedStateHandle.get<String>("symbol").orEmpty()

    private val _state = MutableStateFlow(DetailState(symbol = symbol))
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DetailEffect>(extraBufferCapacity = 8, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val effect: SharedFlow<DetailEffect> = _effect.asSharedFlow()

    fun onIntent(intent: DetailIntent) {
        when (intent) {
            DetailIntent.Load -> load()
            is DetailIntent.SelectTab -> _state.value = _state.value.copy(selectedTab = intent.tab)
            is DetailIntent.ChangeResolution -> changeResolution(intent.resolution)
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
            val candlesDeferred = async { getCandlesUseCase(symbol, _state.value.chartResolution.apiValue, from, to) }
            val newsDeferred = async {
                getCompanyNewsUseCase(symbol, isoDate(from * 1000), isoDate(to * 1000))
            }
            awaitAll(quoteDeferred, profileDeferred, metricsDeferred, peersDeferred, candlesDeferred, newsDeferred)

            val quoteResult = quoteDeferred.await()
            val assetType = AssetType.STOCK // Detail is entered from Watchlist/Search, both of which already resolved AssetType; a bare symbol string alone can't recover it, so price formatting below defaults to STOCK's $ format, and Task 51 threads the real AssetType through the nav arg as a follow-up if forex detail screens need FX-formatted headers.

            _state.value = _state.value.copy(
                isLoading = false,
                quote = (quoteResult as? AppResult.Success)?.data?.let { quote ->
                    QuoteUi(
                        price = formatPrice(quote.price, assetType),
                        change = formatPrice(quote.change, assetType),
                        changePercent = formatPercent(quote.changePercent),
                        changeDirection = changeDirectionOf(quote.changePercent),
                        high = formatPrice(quote.high, assetType),
                        low = formatPrice(quote.low, assetType),
                        open = formatPrice(quote.open, assetType),
                        quoteSource = when (quote.source) {
                            com.sypark.finnhub.core.domain.model.QuoteSource.WEBSOCKET -> UiQuoteSource.WEBSOCKET
                            com.sypark.finnhub.core.domain.model.QuoteSource.REST -> UiQuoteSource.REST
                            com.sypark.finnhub.core.domain.model.QuoteSource.CACHE -> UiQuoteSource.CACHE
                        },
                    )
                },
                profile = (profileDeferred.await() as? AppResult.Success)?.data?.let {
                    StockProfileUi(it.name, it.exchange, it.industry, it.logoUrl, "$${formatLargeNumber(it.marketCapitalization)}", it.webUrl)
                },
                metrics = (metricsDeferred.await() as? AppResult.Success)?.data?.let {
                    StockMetricsUi(
                        peRatioText = it.peRatio?.toString() ?: "—",
                        week52HighText = it.week52High?.let { h -> formatPrice(h, assetType) } ?: "—",
                        week52LowText = it.week52Low?.let { l -> formatPrice(l, assetType) } ?: "—",
                    )
                },
                peers = (peersDeferred.await() as? AppResult.Success)?.data ?: emptyList(),
                candles = (candlesDeferred.await() as? AppResult.Success)?.data?.map { CandleUi(it.timestamp, it.open, it.high, it.low, it.close) } ?: emptyList(),
                news = (newsDeferred.await() as? AppResult.Success)?.data?.map { NewsUi(it.headline, it.source, it.url, it.imageUrl, it.datetime) } ?: emptyList(),
                error = (quoteResult as? AppResult.Error)?.error,
            )
        }
    }

    private fun changeResolution(resolution: ChartResolution) {
        _state.value = _state.value.copy(chartResolution = resolution)
        viewModelScope.launch {
            val to = System.currentTimeMillis() / 1000
            val from = to - TimeUnit.DAYS.toSeconds(90)
            val result = getCandlesUseCase(symbol, resolution.apiValue, from, to)
            if (result is AppResult.Success) {
                _state.value = _state.value.copy(candles = result.data.map { CandleUi(it.timestamp, it.open, it.high, it.low, it.close) })
            }
        }
    }

    private fun toggleWatchlist() {
        viewModelScope.launch {
            val profile = _state.value.profile
            val item = WatchlistItem(
                symbol = symbol,
                displayName = profile?.name ?: symbol,
                assetType = AssetType.STOCK,
                sortOrder = 0,
            )
            when (toggleWatchlistUseCase(item)) {
                is AppResult.Success -> _state.value = _state.value.copy(isInWatchlist = !_state.value.isInWatchlist)
                is AppResult.Error -> _effect.tryEmit(DetailEffect.ShowSnackbar("관심종목 변경에 실패했습니다"))
            }
        }
    }

    private fun isoDate(epochMillis: Long): String =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(epochMillis))
}
