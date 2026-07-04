// core/ui/src/main/java/com/sypark/finnhub/core/ui/component/QuoteSourceIndicator.kt
package com.sypark.finnhub.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.ui.model.UiQuoteSource
import com.sypark.finnhub.core.ui.theme.AppTheme
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme

@Composable
fun QuoteSourceIndicator(
    source: UiQuoteSource,
    modifier: Modifier = Modifier,
) {
    val extended = AppTheme.extended
    val (color, description) = when (source) {
        UiQuoteSource.WEBSOCKET -> extended.live to "실시간"
        UiQuoteSource.REST -> extended.delayed to "지연"
        UiQuoteSource.CACHE -> extended.offline to "캐시"
    }
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .size(8.dp)
            .background(color = color, shape = CircleShape)
            .semantics { contentDescription = description },
    )
}

@Preview
@Composable
private fun QuoteSourceIndicatorPreview() {
    FinnhubQuotesTheme { QuoteSourceIndicator(source = UiQuoteSource.WEBSOCKET) }
}
