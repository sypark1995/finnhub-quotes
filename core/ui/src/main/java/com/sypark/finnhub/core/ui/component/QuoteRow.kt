// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/QuoteRow.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.ui.model.ChangeDirection
import com.sypark.finnhub.core.ui.model.UiQuoteSource
import com.sypark.finnhub.core.ui.theme.AppTheme
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme
import com.sypark.finnhub.core.ui.theme.PriceTypographyMedium
import com.sypark.finnhub.core.ui.theme.ShapeCard
import com.sypark.finnhub.core.ui.theme.ShapeSmall
import com.sypark.finnhub.core.ui.theme.Spacing
import com.sypark.finnhub.core.ui.util.ellipsizeMiddle
import com.sypark.finnhub.core.ui.util.priceContentDescription
import kotlinx.coroutines.delay

private fun assetBadgeLabel(assetType: AssetType): String = when (assetType) {
    AssetType.STOCK -> "ST"
    AssetType.FOREX -> "FX"
    AssetType.CRYPTO -> "CR"
}

@Composable
fun QuoteRow(
    symbol: String,
    displayName: String,
    assetType: AssetType,
    price: String,
    changePercent: String,
    changeDirection: ChangeDirection,
    quoteSource: UiQuoteSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val extended = AppTheme.extended
    // 150ms flash on every `price` change (ui-design.md §4.1, §7 "Price tick flash").
    var flashing by remember { mutableStateOf(false) }
    LaunchedEffect(price) {
        flashing = true
        delay(150)
        flashing = false
    }
    val cardColor = MaterialTheme.colorScheme.surfaceVariant
    val flashTargetColor = when (changeDirection) {
        ChangeDirection.UP -> extended.gainContainer
        ChangeDirection.DOWN -> extended.lossContainer
        ChangeDirection.FLAT -> cardColor
    }
    val backgroundColor by animateColorAsState(
        targetValue = if (flashing) flashTargetColor.copy(alpha = 0.5f) else cardColor,
        animationSpec = tween(durationMillis = 150),
        label = "quoteRowFlash",
    )
    val changeColor = when (changeDirection) {
        ChangeDirection.UP -> extended.gain
        ChangeDirection.DOWN -> extended.loss
        ChangeDirection.FLAT -> extended.neutral
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Spacing.quoteRowHeight)
            .clip(ShapeCard)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.space4)
            .semantics(mergeDescendants = true) {
                contentDescription = priceContentDescription(displayName, price, changePercent, changeDirection)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = ShapeSmall),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = assetBadgeLabel(assetType),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.space3),
        ) {
            Text(
                text = ellipsizeMiddle(symbol, maxLength = 12),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val density = androidx.compose.ui.platform.LocalDensity.current
        if (density.fontScale >= 1.3f) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = price, style = PriceTypographyMedium, color = MaterialTheme.colorScheme.onBackground)
                QuoteSourceIndicator(source = quoteSource)
                Text(text = changePercent, style = MaterialTheme.typography.labelLarge, color = changeColor)
            }
        } else {
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.space1)) {
                    Text(text = price, style = PriceTypographyMedium, color = MaterialTheme.colorScheme.onBackground)
                    QuoteSourceIndicator(source = quoteSource)
                }
                Text(text = changePercent, style = MaterialTheme.typography.labelLarge, color = changeColor)
            }
        }
    }
}

@Preview
@Composable
private fun QuoteRowPreview() {
    FinnhubQuotesTheme {
        QuoteRow(
            symbol = "AAPL",
            displayName = "Apple Inc.",
            assetType = AssetType.STOCK,
            price = "$198.50",
            changePercent = "+1.17%",
            changeDirection = ChangeDirection.UP,
            quoteSource = UiQuoteSource.WEBSOCKET,
            onClick = {},
        )
    }
}
