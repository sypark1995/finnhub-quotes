package com.sypark.finnhub.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val count: Int,
    val result: List<SearchResultDto>,
)

@Serializable
data class SearchResultDto(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String,
)
