package com.sypark.finnhub.settings

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.ThemeMode
import com.sypark.finnhub.core.domain.usecase.settings.ObserveThemeModeUseCase
import com.sypark.finnhub.core.domain.usecase.settings.SetThemeModeUseCase
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
    private val setThemeModeUseCase = mockk<SetThemeModeUseCase>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { observeThemeModeUseCase() } returns flowOf(ThemeMode.SYSTEM)
    }

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `ThemeChanged calls the use case with the new mode`() = runTest(dispatcher) {
        coEvery { setThemeModeUseCase(ThemeMode.DARK) } returns AppResult.Success(Unit)
        val viewModel = SettingsViewModel(observeThemeModeUseCase, setThemeModeUseCase)

        viewModel.onIntent(SettingsIntent.ThemeChanged(ThemeMode.DARK))
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { setThemeModeUseCase(ThemeMode.DARK) }
    }

    @Test
    fun `state reflects the observed theme mode`() = runTest(dispatcher) {
        val viewModel = SettingsViewModel(observeThemeModeUseCase, setThemeModeUseCase)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ThemeMode.SYSTEM, viewModel.state.value.themeMode)
    }
}
