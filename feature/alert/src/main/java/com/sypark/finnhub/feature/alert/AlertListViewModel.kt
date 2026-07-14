package com.sypark.finnhub.feature.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.usecase.alert.DeleteAlertUseCase
import com.sypark.finnhub.core.domain.usecase.alert.ObserveAlertsUseCase
import com.sypark.finnhub.core.domain.usecase.alert.UpdateAlertUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AlertListViewModel @Inject constructor(
    private val observeAlertsUseCase: ObserveAlertsUseCase,
    private val updateAlertUseCase: UpdateAlertUseCase,
    private val deleteAlertUseCase: DeleteAlertUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AlertListState())
    val state: StateFlow<AlertListState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AlertListEffect>(extraBufferCapacity = 8, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val effect: SharedFlow<AlertListEffect> = _effect.asSharedFlow()

    private var latestDomainAlerts: List<PriceAlert> = emptyList()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    fun onIntent(intent: AlertListIntent) {
        when (intent) {
            AlertListIntent.Load -> load()
            is AlertListIntent.ToggleEnabled -> toggleEnabled(intent.id, intent.enabled)
            is AlertListIntent.Delete -> viewModelScope.launch { deleteAlertUseCase(intent.id) }
            AlertListIntent.OpenCreate -> _effect.tryEmit(AlertListEffect.NavigateToCreate)
        }
    }

    private fun load() {
        observeAlertsUseCase()
            .onEach { alerts ->
                latestDomainAlerts = alerts
                _state.value = AlertListState(
                    alerts = alerts.map { it.toUi() },
                    isLoading = false,
                )
            }
            .launchIn(viewModelScope)
    }

    private fun toggleEnabled(id: Long, enabled: Boolean) {
        val target = latestDomainAlerts.find { it.id == id } ?: return
        viewModelScope.launch { updateAlertUseCase(target.copy(isEnabled = enabled)) }
    }

    private fun PriceAlert.toUi(): PriceAlertUi {
        val conditionWord = if (condition == AlertCondition.ABOVE) "이상" else "이하"
        return PriceAlertUi(
            id = id,
            symbol = symbol,
            conditionText = "${targetPrice} ${conditionWord} 도달 시 알림",
            isEnabled = isEnabled,
            triggeredText = triggeredAt?.let { "알림 발송됨 · ${dateFormat.format(Date(it))}" },
        )
    }
}
