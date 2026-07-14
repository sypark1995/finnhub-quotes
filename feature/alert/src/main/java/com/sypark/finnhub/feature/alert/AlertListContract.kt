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
    data object NavigateToCreate : AlertListEffect
}
