package com.sypark.finnhub.feature.earnings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.core.common.getOrNull
import com.sypark.finnhub.core.domain.model.EarningsEvent
import com.sypark.finnhub.core.domain.usecase.earnings.GetEarningsCalendarUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveWatchlistUseCase
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// Caps how many calendar/earnings requests are in flight at once, independent of watchlist
// size. Without this, a 20-symbol watchlist fires 20 simultaneous requests and Finnhub's
// free-tier rate limit turns most of them into 429s that RateLimitInterceptor then retries
// serially anyway -- bounding concurrency upfront avoids manufacturing that retry storm.
private const val MAX_CONCURRENT_EARNINGS_REQUESTS = 5

@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val observeWatchlistUseCase: ObserveWatchlistUseCase,
    private val getEarningsCalendarUseCase: GetEarningsCalendarUseCase,
) : ViewModel() {

    private val requestSemaphore = Semaphore(MAX_CONCURRENT_EARNINGS_REQUESTS)
    private val _state = MutableStateFlow(EarningsState())
    val state: StateFlow<EarningsState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EarningsEffect>(extraBufferCapacity = 8, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val effect: SharedFlow<EarningsEffect> = _effect.asSharedFlow()

    fun onIntent(intent: EarningsIntent) {
        when (intent) {
            EarningsIntent.Load -> load()
            is EarningsIntent.OpenDetail -> _effect.tryEmit(EarningsEffect.NavigateToDetail(intent.symbol))
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val watchlistItems = observeWatchlistUseCase().first()
            if (watchlistItems.isEmpty()) {
                _state.value = EarningsState(events = emptyList(), isLoading = false)
                return@launch
            }

            val displayNameBySymbol = watchlistItems.associate { it.symbol to it.displayName }
            val from = isoDate(System.currentTimeMillis())
            val to = isoDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365))

            // One call per watchlist symbol, in parallel: Finnhub's calendar/earnings has no
            // "only these symbols" filter, and the unfiltered response covers every public
            // company in the date range (hundreds of rows even for a two-week window) -- fetching
            // per-symbol is the only way to keep this bounded to what the user actually watches.
            val events = watchlistItems
                .map { item ->
                    async {
                        requestSemaphore.withPermit { getEarningsCalendarUseCase(from, to, item.symbol).getOrNull() }
                    }
                }
                .awaitAll()
                .filterNotNull()
                .flatten()
                .sortedBy { it.date }
                .map { it.toUi(displayNameBySymbol[it.symbol] ?: it.symbol) }

            _state.value = EarningsState(events = events, isLoading = false)
        }
    }

    private fun isoDate(epochMillis: Long): String =
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(epochMillis))

    private fun EarningsEvent.toUi(displayName: String): EarningsEventUi = EarningsEventUi(
        symbol = symbol,
        displayName = displayName,
        dateText = date.replace("-", "."),
        timingText = when (hour) {
            "bmo" -> "장 시작 전"
            "amc" -> "장 마감 후"
            else -> ""
        },
        epsEstimateText = epsEstimate?.let { "EPS 예상 %.2f".format(java.util.Locale.US, it) } ?: "EPS 예상치 없음",
    )
}
