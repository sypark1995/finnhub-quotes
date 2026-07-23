package com.sypark.finnhub.core.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sypark.finnhub.core.ui.theme.AppTheme
import com.sypark.finnhub.core.ui.theme.FinnhubQuotesTheme

@Composable
fun CandlestickChart(
    candles: List<CandlestickChartEntry>,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 280.dp,
) {
    val extended = AppTheme.extended
    val outlineColor = MaterialTheme.colorScheme.outline

    if (candles.isEmpty()) {
        androidx.compose.foundation.layout.Box(
            modifier = modifier.fillMaxWidth().height(height),
            contentAlignment = Alignment.Center,
        ) {
            Text("차트 데이터가 없습니다", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val maxHigh = candles.maxOf { it.high }
    val minLow = candles.minOf { it.low }
    val range = (maxHigh - minLow).takeIf { it > 0.0 } ?: 1.0

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        // Grid lines (ui-design.md §5.3 "Grid lines | outline 30% alpha")
        val gridLineColor = outlineColor.copy(alpha = 0.3f)
        repeat(4) { i ->
            val y = size.height * i / 4f
            drawLine(gridLineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        }

        val slotWidth = size.width / candles.size
        val bodyWidth = slotWidth * 0.6f

        fun yFor(value: Double): Float = size.height - ((value - minLow) / range * size.height).toFloat()

        candles.forEachIndexed { index, candle ->
            val centerX = slotWidth * index + slotWidth / 2f
            val isGain = candle.close >= candle.open
            val color = if (isGain) extended.gain else extended.loss

            // Wick: high → low
            drawLine(
                color = color,
                start = Offset(centerX, yFor(candle.high)),
                end = Offset(centerX, yFor(candle.low)),
                strokeWidth = 2f,
            )
            // Body: open → close
            val bodyTop = yFor(maxOf(candle.open, candle.close))
            val bodyBottom = yFor(minOf(candle.open, candle.close))
            drawRect(
                color = color,
                topLeft = Offset(centerX - bodyWidth / 2f, bodyTop),
                size = androidx.compose.ui.geometry.Size(bodyWidth, (bodyBottom - bodyTop).coerceAtLeast(2f)),
            )
        }
    }
}

@Preview
@Composable
private fun CandlestickChartPreview() {
    FinnhubQuotesTheme {
        CandlestickChart(
            candles = listOf(
                CandlestickChartEntry(1L, open = 196.0, high = 199.0, low = 195.0, close = 198.5),
                CandlestickChartEntry(2L, open = 198.5, high = 200.0, low = 197.0, close = 197.2),
            ),
        )
    }
}
