package com.sypark.finnhub.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.BuildConfig
import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.domain.usecase.settings.ObserveThemeModeUseCase
import com.sypark.finnhub.core.domain.usecase.settings.SetThemeModeUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveConnectionStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeModeUseCase: ObserveThemeModeUseCase,
    observeConnectionStatusUseCase: ObserveConnectionStatusUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(versionName = BuildConfig.VERSION_NAME))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeThemeModeUseCase()
            .onEach { mode -> _state.value = _state.value.copy(themeMode = mode) }
            .launchIn(viewModelScope)
        // apiStatus previously stayed at its UNKNOWN default forever ("확인 중" / "checking...")
        // because nothing ever updated it -- there was no health check behind the label at all.
        // The WebSocket connection state is the one live signal this app already tracks for API
        // reachability, so reuse it instead of adding a separate dedicated health-check call.
        observeConnectionStatusUseCase()
            .onEach { status -> _state.value = _state.value.copy(apiStatus = status.toApiStatus()) }
            .launchIn(viewModelScope)
    }

    private fun ConnectionStatus.toApiStatus(): ApiStatus = when (this) {
        ConnectionStatus.Connected -> ApiStatus.OK
        ConnectionStatus.Connecting, ConnectionStatus.Reconnecting, ConnectionStatus.Disconnected -> ApiStatus.DEGRADED
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ThemeChanged -> viewModelScope.launch { setThemeModeUseCase(intent.mode) }
        }
    }
}
