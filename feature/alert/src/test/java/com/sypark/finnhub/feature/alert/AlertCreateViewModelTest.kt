package com.sypark.finnhub.feature.alert

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.usecase.alert.CreateAlertUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertCreateViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val createAlertUseCase = mockk<CreateAlertUseCase>()

    @BeforeEach
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel() = AlertCreateViewModel(SavedStateHandle(mapOf("symbol" to "AAPL")), createAlertUseCase)

    @Test
    fun `Save with a non-positive price sets priceError and does not call the use case`() = runTest(dispatcher) {
        val viewModel = buildViewModel()
        viewModel.onIntent(AlertCreateIntent.TargetPriceChanged("0"))

        viewModel.onIntent(AlertCreateIntent.Save)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.state.value.priceError != null)
        io.mockk.coVerify(exactly = 0) { createAlertUseCase(any(), any(), any()) }
    }

    @Test
    fun `Save with a blank symbol shows a snackbar and does not call the use case`() = runTest(dispatcher) {
        val viewModel = AlertCreateViewModel(SavedStateHandle(emptyMap()), createAlertUseCase)
        viewModel.onIntent(AlertCreateIntent.TargetPriceChanged("210.0"))

        viewModel.effect.test {
            viewModel.onIntent(AlertCreateIntent.Save)
            val effect = awaitItem()
            assertEquals(true, effect is AlertCreateEffect.ShowSnackbar)
        }
        io.mockk.coVerify(exactly = 0) { createAlertUseCase(any(), any(), any()) }
    }

    @Test
    fun `Save with a valid price calls the use case and emits Dismiss`() = runTest(dispatcher) {
        coEvery { createAlertUseCase("AAPL", 210.0, com.sypark.finnhub.core.common.AlertCondition.ABOVE) } returns AppResult.Success(1L)
        val viewModel = buildViewModel()
        viewModel.onIntent(AlertCreateIntent.TargetPriceChanged("210.0"))

        viewModel.effect.test {
            viewModel.onIntent(AlertCreateIntent.Save)
            assertEquals(AlertCreateEffect.Dismiss, awaitItem())
        }
    }
}
