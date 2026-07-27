package com.sypark.finnhub.feature.alert

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.usecase.alert.CreateAlertUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertCreateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createAlertUseCase: CreateAlertUseCase,
) : ViewModel() {

    private val symbol: String = savedStateHandle.get<String>("symbol").orEmpty()

    private val _state = MutableStateFlow(AlertCreateState(symbol = symbol))
    val state: StateFlow<AlertCreateState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AlertCreateEffect>(extraBufferCapacity = 8, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val effect: SharedFlow<AlertCreateEffect> = _effect.asSharedFlow()

    fun onIntent(intent: AlertCreateIntent) {
        when (intent) {
            is AlertCreateIntent.TargetPriceChanged -> _state.value = _state.value.copy(targetPriceInput = intent.value, priceError = null)
            is AlertCreateIntent.ConditionChanged -> _state.value = _state.value.copy(condition = intent.condition)
            AlertCreateIntent.Save -> save()
        }
    }

    private fun save() {
        val price = _state.value.targetPriceInput.toDoubleOrNull()
        if (price == null || price <= 0.0) {
            _state.value = _state.value.copy(priceError = "목표가는 0보다 커야 합니다")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            when (createAlertUseCase(symbol, price, _state.value.condition)) {
                is AppResult.Success -> _effect.tryEmit(AlertCreateEffect.Dismiss)
                is AppResult.Error -> {
                    _state.value = _state.value.copy(isSaving = false)
                    _effect.tryEmit(AlertCreateEffect.ShowSnackbar("알림 저장에 실패했습니다"))
                }
            }
        }
    }
}
