package com.sypark.finnhub.feature.alert

import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.usecase.alert.DeleteAlertUseCase
import com.sypark.finnhub.core.domain.usecase.alert.ObserveAlertsUseCase
import com.sypark.finnhub.core.domain.usecase.alert.UpdateAlertUseCase
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
class AlertListViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val observeAlertsUseCase = mockk<ObserveAlertsUseCase>()
    private val updateAlertUseCase = mockk<UpdateAlertUseCase>()
    private val deleteAlertUseCase = mockk<DeleteAlertUseCase>()

    @BeforeEach
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel() = AlertListViewModel(observeAlertsUseCase, updateAlertUseCase, deleteAlertUseCase)

    @Test
    fun `Load formats every alert into a PriceAlertUi with a condition sentence`() = runTest(dispatcher) {
        every { observeAlertsUseCase() } returns flowOf(listOf(PriceAlert(1, "AAPL", 210.0, AlertCondition.ABOVE, true, null)))
        val viewModel = buildViewModel()

        viewModel.onIntent(AlertListIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        val alert = viewModel.state.value.alerts.single()
        assertEquals("AAPL", alert.symbol)
        assertEquals(true, alert.conditionText.contains("이상"))
        assertEquals(null, alert.triggeredText)
    }

    @Test
    fun `ToggleEnabled calls update with the flipped isEnabled`() = runTest(dispatcher) {
        every { observeAlertsUseCase() } returns flowOf(listOf(PriceAlert(1, "AAPL", 210.0, AlertCondition.ABOVE, true, null)))
        coEvery { updateAlertUseCase(any()) } returns AppResult.Success(Unit)
        val viewModel = buildViewModel()
        viewModel.onIntent(AlertListIntent.Load)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(AlertListIntent.ToggleEnabled(1L, enabled = false))
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { updateAlertUseCase(PriceAlert(1, "AAPL", 210.0, AlertCondition.ABOVE, false, null)) }
    }

    @Test
    fun `Delete calls the use case with the given id`() = runTest(dispatcher) {
        coEvery { deleteAlertUseCase(1L) } returns AppResult.Success(Unit)
        val viewModel = buildViewModel()

        viewModel.onIntent(AlertListIntent.Delete(1L))
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { deleteAlertUseCase(1L) }
    }
}
