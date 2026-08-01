package com.sypark.finnhub.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class EarningsCalendarResponseDto(val earningsCalendar: List<EarningsEventDto> = emptyList())

@Serializable
data class EarningsEventDto(
    val date: String,
    val hour: String = "",
    val epsEstimate: Double? = null,
    val epsActual: Double? = null,
    val revenueEstimate: Double? = null,
    val revenueActual: Double? = null,
    val symbol: String,
)
