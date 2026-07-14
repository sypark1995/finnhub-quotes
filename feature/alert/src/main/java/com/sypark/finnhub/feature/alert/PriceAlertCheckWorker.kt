package com.sypark.finnhub.feature.alert

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.usecase.alert.MarkAlertTriggeredUseCase
import com.sypark.finnhub.core.domain.usecase.alert.ObserveAlertsUseCase
import com.sypark.finnhub.core.domain.usecase.detail.GetQuoteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class PriceAlertCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val observeAlertsUseCase: ObserveAlertsUseCase,
    private val getQuoteUseCase: GetQuoteUseCase,
    private val markAlertTriggeredUseCase: MarkAlertTriggeredUseCase,
    private val notificationHelper: NotificationHelper,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val activeAlerts = observeAlertsUseCase().first().filter { it.isEnabled && it.triggeredAt == null }

        activeAlerts.forEach { alert ->
            val quoteResult = getQuoteUseCase(alert.symbol)
            if (quoteResult !is AppResult.Success) return@forEach

            val price = quoteResult.data.price
            val conditionMet = when (alert.condition) {
                AlertCondition.ABOVE -> price >= alert.targetPrice
                AlertCondition.BELOW -> price <= alert.targetPrice
            }
            if (conditionMet) {
                notificationHelper.showAlertTriggeredNotification(alert.id, alert.symbol, alert.targetPrice, alert.condition)
                markAlertTriggeredUseCase(alert.id)
            }
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "price_alert_check"
    }
}
