package com.sypark.finnhub.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompanyNewsDto(
    val id: Long,
    val headline: String,
    val source: String,
    val url: String,
    val datetime: Long,
    val summary: String,
    val image: String,
)
