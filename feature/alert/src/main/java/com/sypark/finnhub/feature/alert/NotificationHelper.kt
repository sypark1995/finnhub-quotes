package com.sypark.finnhub.feature.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sypark.finnhub.core.common.AlertCondition
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun showAlertTriggeredNotification(alertId: Long, symbol: String, targetPrice: Double, condition: AlertCondition) {
        ensureChannel()
        val conditionWord = when (condition) {
            AlertCondition.ABOVE -> "이상"
            AlertCondition.BELOW -> "이하"
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("가격 알림")
            .setContentText("${symbol}이(가) ${targetPrice} ${conditionWord} 도달")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(alertId.toInt(), notification)
    }

    private fun ensureChannel() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "가격 알림", NotificationManager.IMPORTANCE_HIGH),
            )
        }
    }

    companion object {
        const val CHANNEL_ID = "price_alerts"
    }
}
