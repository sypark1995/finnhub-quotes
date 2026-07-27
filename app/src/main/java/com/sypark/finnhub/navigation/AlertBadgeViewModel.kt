package com.sypark.finnhub.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.core.domain.usecase.alert.ObserveAlertsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlertBadgeViewModel @Inject constructor(
    observeAlertsUseCase: ObserveAlertsUseCase,
) : ViewModel() {
    val enabledAlertCount: StateFlow<Int> = observeAlertsUseCase()
        .map { alerts -> alerts.count { it.isEnabled && it.triggeredAt == null } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
