package com.sypark.finnhub.core.ui.util

import com.sypark.finnhub.core.ui.model.ChangeDirection

fun priceContentDescription(displayName: String, price: String, changePercent: String, direction: ChangeDirection): String {
    val directionWord = when (direction) {
        ChangeDirection.UP -> "상승"
        ChangeDirection.DOWN -> "하락"
        ChangeDirection.FLAT -> "보합"
    }
    val cleanPercent = changePercent.removePrefix("+").removePrefix("-").removeSuffix("%")
    return "$displayName, $price, $cleanPercent 퍼센트 $directionWord"
}
