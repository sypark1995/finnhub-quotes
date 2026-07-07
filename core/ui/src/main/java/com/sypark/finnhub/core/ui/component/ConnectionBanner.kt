// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/ConnectionBanner.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.ui.model.ConnectionBannerState
import com.sypark.finnhub.core.ui.theme.AppTheme
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.core.ui.theme.ShapeCard
import com.sypark.finnhub.core.ui.theme.Spacing

private data class BannerSpec(val backgroundAlphaColor: androidx.compose.ui.graphics.Color, val dotColor: androidx.compose.ui.graphics.Color, val text: String, val pulsing: Boolean)

@Composable
fun ConnectionBanner(
    state: ConnectionBannerState,
    modifier: Modifier = Modifier,
) {
    val extended = AppTheme.extended
    val spec = when (state) {
        ConnectionBannerState.LIVE -> BannerSpec(extended.gainContainer.copy(alpha = 0.2f), extended.live, "실시간 연결됨", pulsing = false)
        ConnectionBannerState.RECONNECTING -> BannerSpec(extended.delayed.copy(alpha = 0.15f), extended.delayed, "재연결 중…", pulsing = true)
        ConnectionBannerState.DELAYED -> BannerSpec(MaterialTheme.colorScheme.surfaceVariant, extended.offline, "지연 시세 · REST", pulsing = false)
        ConnectionBannerState.OFFLINE -> BannerSpec(MaterialTheme.colorScheme.surfaceVariant, extended.offline, "오프라인 · 캐시 표시", pulsing = false)
    }

    AnimatedContent(
        targetState = spec,
        modifier = modifier,
        label = "connectionBanner",
    ) { targetSpec ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.space4, vertical = Spacing.space2)
                .height(40.dp)
                .clip(ShapeCard)
                .background(targetSpec.backgroundAlphaColor)
                .padding(horizontal = Spacing.space4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val scale = if (targetSpec.pulsing) {
                val transition = rememberInfiniteTransition(label = "connectionDotPulse")
                val animatedScale by transition.animateFloat(
                    initialValue = 1.0f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "connectionDotPulseScale",
                )
                animatedScale
            } else {
                1.0f
            }
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .background(color = targetSpec.dotColor, shape = CircleShape),
            )
            Text(
                text = targetSpec.text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = Spacing.space2),
            )
        }
    }
}

@Preview
@Composable
private fun ConnectionBannerPreview() {
    FinnhubQuotesTheme { ConnectionBanner(state = ConnectionBannerState.LIVE) }
}
