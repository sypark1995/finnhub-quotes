package com.sypark.finnhub.core.ui.util

fun ellipsizeMiddle(text: String, maxLength: Int): String {
    if (text.length <= maxLength) return text
    val keep = (maxLength - 1) / 2
    val head = text.take(keep)
    val tail = text.takeLast(maxLength - keep - 1)
    return "$head…$tail"
}
