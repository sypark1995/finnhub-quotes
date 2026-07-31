package com.sypark.finnhub.settings

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.ConnectionStatus
import com.sypark.finnhub.core.domain.model.ThemeMode
import com.sypark.finnhub.core.domain.usecase.settings.ObserveThemeModeUseCase
import com.sypark.finnhub.core.domain.usecase.settings.SetThemeModeUseCase
import com.sypark.finnhub.core.domain.usecase.watchlist.ObserveConnectionStatusUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val observeThemeModeUseCase = mockk<ObserveThemeModeUseCase>()
    private val observeConnectionStatusUseCase = mockk<ObserveConnectionStatusUseCase>()
    private val setThemeModeUseCase = mockk<SetThemeModeUseCase>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { observeThemeModeUseCase() } returns flowOf(ThemeMode.SYSTEM)
        every { observeConnectionStatusUseCase() } returns flowOf(ConnectionStatus.Connected)
    }

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel() = SettingsViewModel(observeThemeModeUseCase, observeConnectionStatusUseCase, setThemeModeUseCase)

    @Test
    fun `ThemeChanged calls the use case with the new mode`() = runTest(dispatcher) {
        coEvery { setThemeModeUseCase(ThemeMode.DARK) } returns AppResult.Success(Unit)
        val viewModel = buildViewModel()

        viewModel.onIntent(SettingsIntent.ThemeChanged(ThemeMode.DARK))
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { setThemeModeUseCase(ThemeMode.DARK) }
    }

    @Test
    fun `state reflects the observed theme mode`() = runTest(dispatcher) {
        val viewModel = buildViewModel()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ThemeMode.SYSTEM, viewModel.state.value.themeMode)
    }

    @Test
    fun `apiStatus is OK when the websocket connection is Connected`() = runTest(dispatcher) {
        val viewModel = buildViewModel()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ApiStatus.OK, viewModel.state.value.apiStatus)
    }

    @Test
    fun `apiStatus is DEGRADED when the websocket connection is not Connected`() = runTest(dispatcher) {
        every { observeConnectionStatusUseCase() } returns flowOf(ConnectionStatus.Disconnected)
        val viewModel = buildViewModel()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ApiStatus.DEGRADED, viewModel.state.value.apiStatus)
    }
}
