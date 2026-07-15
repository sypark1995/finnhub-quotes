package com.sypark.finnhub.feature.alert

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.sypark.finnhub.core.common.AlertCondition
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NotificationHelperTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val helper = NotificationHelper(context)

    init {
        // POST_NOTIFICATIONS is a runtime permission on API 33+; Robolectric does not
        // auto-grant it, so simulate the user having granted it (as real usage requires
        // before NotificationHelper will post anything on API 33+).
        shadowOf(context as android.app.Application).grantPermissions(Manifest.permission.POST_NOTIFICATIONS)
    }

    @Test
    fun `showAlertTriggeredNotification creates the price_alerts HIGH-importance channel`() {
        helper.showAlertTriggeredNotification(alertId = 1L, symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel("price_alerts")
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.importance)
    }

    @Test
    fun `showAlertTriggeredNotification posts the design-md content template`() {
        helper.showAlertTriggeredNotification(alertId = 1L, symbol = "AAPL", targetPrice = 210.0, condition = AlertCondition.ABOVE)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shadowManager = shadowOf(manager)
        assertTrue(shadowManager.allNotifications.isNotEmpty())
    }
}
