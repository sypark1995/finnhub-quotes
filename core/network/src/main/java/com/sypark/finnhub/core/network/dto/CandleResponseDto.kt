package com.sypark.finnhub.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CandleResponseDto(
    val c: List<Double> = emptyList(),
    val h: List<Double> = emptyList(),
    val l: List<Double> = emptyList(),
    val o: List<Double> = emptyList(),
    val s: String,
    val t: List<Long> = emptyList(),
    val v: List<Long> = emptyList(),
)
