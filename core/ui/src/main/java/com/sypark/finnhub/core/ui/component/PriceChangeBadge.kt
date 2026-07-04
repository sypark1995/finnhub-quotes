// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/PriceChangeBadge.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.ui.theme.AppTheme
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.core.ui.theme.ShapeExtraSmall
import com.sypark.finnhub.core.ui.theme.Spacing
import com.sypark.finnhub.core.ui.util.formatPercent

@Composable
fun PriceChangeBadge(
    changePercent: Double,
    modifier: Modifier = Modifier,
) {
    val extended = AppTheme.extended
    val (containerColor, contentColor) = when {
        changePercent > 0.0 -> extended.gainContainer to extended.gain
        changePercent < 0.0 -> extended.lossContainer to extended.loss
        else -> MaterialTheme.colorScheme.surfaceVariant to extended.neutral
    }
    Text(
        text = formatPercent(changePercent),
        color = contentColor,
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        modifier = modifier
            .background(color = containerColor, shape = ShapeExtraSmall)
            .padding(horizontal = Spacing.space2, vertical = 4.dp),
    )
}

@Preview(name = "Gain")
@Composable
private fun PriceChangeBadgeGainPreview() {
    FinnhubQuotesTheme { PriceChangeBadge(changePercent = 1.17) }
}

@Preview(name = "Loss")
@Composable
private fun PriceChangeBadgeLossPreview() {
    FinnhubQuotesTheme { PriceChangeBadge(changePercent = -0.32) }
}
