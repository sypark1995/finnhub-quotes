package com.sypark.finnhub.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.domain.usecase.search.AddToWatchlistUseCase
import com.sypark.finnhub.core.domain.usecase.search.ObserveWatchlistSymbolsUseCase
import com.sypark.finnhub.core.domain.usecase.search.SearchSymbolsUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSymbolsUseCase: SearchSymbolsUseCase,
    private val addToWatchlistUseCase: AddToWatchlistUseCase,
    observeWatchlistSymbolsUseCase: ObserveWatchlistSymbolsUseCase,
    private val removeFromWatchlistUseCase: RemoveFromWatchlistUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SearchEffect>(extraBufferCapacity = 8, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val effect: SharedFlow<SearchEffect> = _effect.asSharedFlow()

    private val queryFlow = MutableStateFlow("")
    private var rawResults: List<SearchResult> = emptyList()

    init {
        observeWatchlistSymbolsUseCase()
            .onEach { symbols -> _state.value = applyResults(_state.value.copy(watchlistSymbols = symbols)) }
            .launchIn(viewModelScope)

        // design.md §11.2 "SearchSymbolsUseCase (debounce 300ms)" — the debounce lives here,
        // against the keystroke Flow, since the UseCase itself is a plain one-shot call (Task 27).
        queryFlow
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .flatMapLatest { query ->
                _state.value = _state.value.copy(isSearching = true)
                kotlinx.coroutines.flow.flow { emit(searchSymbolsUseCase(query)) }
            }
            .onEach { result ->
                when (result) {
                    is AppResult.Success -> {
                        rawResults = result.data
                        _state.value = applyResults(_state.value.copy(isSearching = false, error = null))
                    }
                    is AppResult.Error -> _state.value = _state.value.copy(isSearching = false, error = result.error)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _state.value = _state.value.copy(query = intent.query)
                if (intent.query.isBlank()) {
                    rawResults = emptyList()
                    _state.value = applyResults(_state.value)
                }
                queryFlow.value = intent.query
            }
            is SearchIntent.FilterChanged -> _state.value = applyResults(_state.value.copy(selectedFilter = intent.filter))
            is SearchIntent.AddToWatchlist -> viewModelScope.launch {
                addToWatchlistUseCase(
                    SearchResult(intent.result.symbol, intent.result.description, intent.result.assetType),
                    sortOrder = _state.value.watchlistSymbols.size,
                )
            }
            is SearchIntent.RemoveFromWatchlist -> viewModelScope.launch { removeFromWatchlistUseCase(intent.symbol) }
            is SearchIntent.OpenDetail -> _effect.tryEmit(SearchEffect.NavigateToDetail(intent.symbol))
        }
    }

    private fun applyResults(state: SearchState): SearchState {
        val filtered = rawResults.filter { state.selectedFilter.matches(it.assetType) }
        return state.copy(
            results = filtered.map {
                SearchResultUi(it.symbol, it.description, it.assetType, isInWatchlist = it.symbol in state.watchlistSymbols)
            },
        )
    }
}
