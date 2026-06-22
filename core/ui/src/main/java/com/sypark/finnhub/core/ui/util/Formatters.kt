package com.sypark.finnhub.core.ui.util

import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.ui.model.ChangeDirection
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

private val usSymbols = DecimalFormatSymbols(Locale.US)
private val usdFormat = DecimalFormat("$#,##0.00", usSymbols)
private val fxFormat = DecimalFormat("0.0000", usSymbols)
private val percentFormat = DecimalFormat("+0.00%;-0.00%", usSymbols)

fun formatPrice(value: Double, assetType: AssetType): String = when (assetType) {
    AssetType.STOCK, AssetType.CRYPTO -> usdFormat.format(value)
    AssetType.FOREX -> fxFormat.format(value)
}

fun formatPercent(value: Double): String {
    // DecimalFormat's +/- pattern above already signs everything except exact zero,
    // which Java formats as "-0.00%" for -0.0 and "+0.00%" for +0.0 — force positive zero.
    val normalized = if (value == 0.0) 0.0 else value
    return percentFormat.format(normalized / 100.0)
}

fun formatLargeNumber(value: Double): String {
    val absValue = abs(value)
    return when {
        absValue >= 1_000_000_000_000.0 -> "%.1fT".format(Locale.US, value / 1_000_000_000_000.0)
        absValue >= 1_000_000_000.0 -> "%.1fB".format(Locale.US, value / 1_000_000_000.0)
        absValue >= 1_000_000.0 -> "%.1fM".format(Locale.US, value / 1_000_000.0)
        else -> "%.1f".format(Locale.US, value)
    }
}

fun changeDirectionOf(changePercent: Double): ChangeDirection = when {
    changePercent > 0.0 -> ChangeDirection.UP
    changePercent < 0.0 -> ChangeDirection.DOWN
    else -> ChangeDirection.FLAT
}
