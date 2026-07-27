package com.sypark.finnhub.feature.alert

import com.sypark.finnhub.core.common.AlertCondition

data class AlertCreateState(
    val symbol: String = "",
    val targetPriceInput: String = "",
    val condition: AlertCondition = AlertCondition.ABOVE,
    val priceError: String? = null,
    val isSaving: Boolean = false,
)

sealed interface AlertCreateIntent {
    data class TargetPriceChanged(val value: String) : AlertCreateIntent
    data class ConditionChanged(val condition: AlertCondition) : AlertCreateIntent
    data object Save : AlertCreateIntent
}

sealed interface AlertCreateEffect {
    data object Dismiss : AlertCreateEffect
    data class ShowSnackbar(val message: String) : AlertCreateEffect
}
