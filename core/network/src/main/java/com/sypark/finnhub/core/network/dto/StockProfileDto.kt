package com.sypark.finnhub.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StockProfileDto(
    val name: String = "",
    val exchange: String = "",
    val finnhubIndustry: String = "",
    val logo: String = "",
    val marketCapitalization: Double = 0.0,
    val weburl: String = "",
    val currency: String = "",
)
