package com.sypark.finnhub.feature.alert

import com.sypark.finnhub.core.common.UiError

data class AlertListState(
    val alerts: List<PriceAlertUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: UiError? = null,
)

sealed interface AlertListIntent {
    data object Load : AlertListIntent
    data class ToggleEnabled(val id: Long, val enabled: Boolean) : AlertListIntent
    data class Delete(val id: Long) : AlertListIntent
    data object OpenCreate : AlertListIntent
}

sealed interface AlertListEffect {
    // "알림 추가" has no symbol picker of its own (a price alert needs a symbol), so it routes
    // through Search first -- matching the empty state's own copy: "관심종목에서 알림을 추가해 보세요".
    // From a search result / Detail screen, the bell icon opens AlertCreate with a real symbol.
    data object NavigateToSearch : AlertListEffect
}
