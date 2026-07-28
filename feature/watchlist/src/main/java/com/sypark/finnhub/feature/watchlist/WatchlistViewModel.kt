package com.sypark.finnhub.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.core.common.AppCoroutineScope
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.WatchlistItem
import com.sypark.finnhub.core.domain.usecase.watchlist.DisconnectMarketUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveConnectionStatusUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveQuotesUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveWatchlistUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.PopularSymbols
import com.sypark.finnhub.core.domain.usecase.watchlist.RefreshQuotesUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ReorderWatchlistUseCase
import com.sypark.finnhub.core.ui.model.UiQuoteSource
import com.sypark.finnhub.core.ui.util.changeDirectionOf
import com.sypark.finnhub.core.ui.util.formatPercent
import com.sypark.finnhub.core.ui.util.formatPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val observeWatchlistUseCase: ObserveWatchlistUseCase,
    private val observeQuotesUseCase: ObserveQuotesUseCase,
    private val observeConnectionStatusUseCase: ObserveConnectionStatusUseCase,
    private val removeFromWatchlistUseCase: RemoveFromWatchlistUseCase,
    private val reorderWatchlistUseCase: ReorderWatchlistUseCase,
    private val refreshQuotesUseCase: RefreshQuotesUseCase,
    private val disconnectMarketUseCase: DisconnectMarketUseCase,
    private val appCoroutineScope: AppCoroutineScope,
) : ViewModel() {

    private val _state = MutableStateFlow(WatchlistState())
    val state: StateFlow<WatchlistState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WatchlistEffect>(extraBufferCapacity = 8, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val effect: SharedFlow<WatchlistEffect> = _effect.asSharedFlow()

    private var latestDomainItems: List<WatchlistItem> = emptyList()

    fun onIntent(intent: WatchlistIntent) {
        when (intent) {
            WatchlistIntent.Load -> load()
            WatchlistIntent.Refresh -> refresh()
            is WatchlistIntent.Remove -> remove(intent.symbol)
            is WatchlistIntent.Reorder -> reorder(intent.fromIndex, intent.toIndex)
            is WatchlistIntent.OpenDetail -> _effect.tryEmit(WatchlistEffect.NavigateToDetail(intent.symbol, intent.assetType))
            WatchlistIntent.OpenSearch -> _effect.tryEmit(WatchlistEffect.NavigateToSearch)
        }
    }

    private fun load() {
        observeWatchlistUseCase()
            .flatMapLatest { items ->
                latestDomainItems = items
                val watchlistSymbols = items.map { it.symbol }.toSet()
                // Union into a single observeQuotesUseCase subscription: the WebSocket
                // manager's syncSubscriptions() replaces the *entire* active symbol set on
                // every call, so two independent subscriptions (watchlist + popular) would
                // keep stomping on each other's symbols.
                combine(
                    observeQuotesUseCase(watchlistSymbols + PopularSymbols.SYMBOLS),
                    observeConnectionStatusUseCase(),
                ) { quotes, connectionStatus ->
                    val assetTypeBySymbol = items.associate { it.symbol to it.assetType }
                    WatchlistState(
                        items = items.map { WatchlistItemUi(it.symbol, it.displayName, it.assetType) },
                        quotes = quotes.filterKeys { it in watchlistSymbols }.mapValues { (symbol, quote) ->
                            QuoteUi(
                                price = formatPrice(quote.price, assetTypeBySymbol.getValue(symbol)),
                                changePercent = formatPercent(quote.changePercent),
                                changeDirection = changeDirectionOf(quote.changePercent),
                                quoteSource = when (quote.source) {
                                    com.sypark.finnhub.core.domain.model.QuoteSource.WEBSOCKET -> UiQuoteSource.WEBSOCKET
                                    com.sypark.finnhub.core.domain.model.QuoteSource.REST -> UiQuoteSource.REST
                                    com.sypark.finnhub.core.domain.model.QuoteSource.CACHE -> UiQuoteSource.CACHE
                                },
                            )
                        },
                        popularStocks = PopularSymbols.ENTRIES.mapNotNull { entry ->
                            quotes[entry.symbol]?.let { quote ->
                                PopularStockUi(
                                    symbol = entry.symbol,
                                    displayName = entry.displayName,
                                    price = formatPrice(quote.price, AssetType.STOCK),
                                    changePercent = formatPercent(quote.changePercent),
                                    changeDirection = changeDirectionOf(quote.changePercent),
                                    changePercentValue = quote.changePercent,
                                )
                            }
                        }.sortedByDescending { kotlin.math.abs(it.changePercentValue) },
                        connectionStatus = connectionStatus,
                        isLoading = false,
                        isRefreshing = _state.value.isRefreshing,
                    )
                }
            }
            .onEach { newState -> _state.value = newState }
            .launchIn(viewModelScope)
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRefreshing = true)
            refreshQuotesUseCase(latestDomainItems.map { it.symbol }.toSet())
            _state.value = _state.value.copy(isRefreshing = false)
        }
    }

    private fun remove(symbol: String) {
        viewModelScope.launch {
            when (val result = removeFromWatchlistUseCase(symbol)) {
                is AppResult.Error -> _effect.tryEmit(WatchlistEffect.ShowSnackbar("삭제에 실패했습니다"))
                is AppResult.Success -> Unit
            }
        }
    }

    private fun reorder(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            reorderWatchlistUseCase(fromIndex, toIndex, latestDomainItems)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Fire-and-forget: ViewModel is being destroyed, viewModelScope is already
        // cancelled at this point, so this uses the injected AppCoroutineScope —
        // a Singleton, Hilt-injectable, SupervisorJob-backed scope that already exists
        // in this codebase for exactly this purpose (see its own doc comment: "Never use
        // GlobalScope — inject this instead"). GlobalScope stays forbidden even in a
        // teardown path (Global Constraints).
        appCoroutineScope.launch {
            disconnectMarketUseCase()
        }
    }
}
