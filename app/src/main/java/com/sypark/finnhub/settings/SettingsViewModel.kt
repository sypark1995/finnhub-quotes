package com.sypark.finnhub.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sypark.finnhub.BuildConfig
import com.sypark.finnhub.core.domain.usecase.settings.ObserveThemeModeUseCase
import com.sypark.finnhub.core.domain.usecase.settings.SetThemeModeUseCase
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
    private val setThemeModeUseCase: SetThemeModeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState(versionName = BuildConfig.VERSION_NAME))
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeThemeModeUseCase()
            .onEach { mode -> _state.value = _state.value.copy(themeMode = mode) }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ThemeChanged -> viewModelScope.launch { setThemeModeUseCase(intent.mode) }
        }
    }
}
