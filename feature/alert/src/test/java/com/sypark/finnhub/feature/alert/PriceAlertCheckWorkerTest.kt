package com.sypark.finnhub.feature.alert

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.model.Quote
import com.sypark.finnhub.core.domain.model.QuoteSource
import com.sypark.finnhub.core.domain.usecase.alert.MarkAlertTriggeredUseCase
import com.sypark.finnhub.core.domain.usecase.alert.ObserveAlertsUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetQuoteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PriceAlertCheckWorkerTest {

    private val observeAlertsUseCase = mockk<ObserveAlertsUseCase>()
    private val getQuoteUseCase = mockk<GetQuoteUseCase>()
    private val markAlertTriggeredUseCase = mockk<MarkAlertTriggeredUseCase>(relaxed = true)
    private val notificationHelper = mockk<NotificationHelper>(relaxUnitFun = true)

    private fun buildWorker(): PriceAlertCheckWorker {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return TestListenableWorkerBuilder<PriceAlertCheckWorker>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker =
                    PriceAlertCheckWorker(appContext, workerParameters, observeAlertsUseCase, getQuoteUseCase, markAlertTriggeredUseCase, notificationHelper)
            })
            .build()
    }

    @Test
    fun `doWork notifies and marks triggered when an ABOVE condition is met`() = runTest {
        every { observeAlertsUseCase() } returns flowOf(listOf(PriceAlert(1, "AAPL", 190.0, AlertCondition.ABOVE, true, null)))
        coEvery { getQuoteUseCase("AAPL") } returns AppResult.Success(
            Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.REST),
        )

        val result = buildWorker().doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { markAlertTriggeredUseCase(1L) }
        verify { notificationHelper.showAlertTriggeredNotification(1L, "AAPL", 190.0, AlertCondition.ABOVE) }
    }

    @Test
    fun `doWork does nothing when the condition is not met`() = runTest {
        every { observeAlertsUseCase() } returns flowOf(listOf(PriceAlert(1, "AAPL", 300.0, AlertCondition.ABOVE, true, null)))
        coEvery { getQuoteUseCase("AAPL") } returns AppResult.Success(
            Quote("AAPL", 198.5, 2.3, 1.17, 199.1, 196.8, 197.2, 196.2, 1L, QuoteSource.REST),
        )

        buildWorker().doWork()

        coVerify(exactly = 0) { markAlertTriggeredUseCase(any()) }
    }

    @Test
    fun `doWork skips disabled and already-triggered alerts`() = runTest {
        every { observeAlertsUseCase() } returns flowOf(
            listOf(
                PriceAlert(1, "AAPL", 100.0, AlertCondition.ABOVE, isEnabled = false, triggeredAt = null),
                PriceAlert(2, "MSFT", 100.0, AlertCondition.ABOVE, isEnabled = true, triggeredAt = 5L),
            ),
        )

        buildWorker().doWork()

        coVerify(exactly = 0) { getQuoteUseCase(any()) }
    }
}
