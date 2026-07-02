package com.sypark.finnhub.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockMetricResponseDto(val metric: StockMetricDto)

@Serializable
data class StockMetricDto(
    @SerialName("peBasicExclExtraTTM") val peRatio: Double? = null,
    @SerialName("52WeekHigh") val week52High: Double? = null,
    @SerialName("52WeekLow") val week52Low: Double? = null,
    @SerialName("epsBasicExclExtraItemsTTM") val epsTTM: Double? = null,
    val beta: Double? = null,
)
