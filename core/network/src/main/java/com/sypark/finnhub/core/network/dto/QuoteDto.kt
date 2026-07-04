package com.sypark.finnhub.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuoteDto(
    val c: Double,
    val d: Double,
    val dp: Double,
    val h: Double,
    val l: Double,
    val o: Double,
    val pc: Double,
    val t: Long,
)
