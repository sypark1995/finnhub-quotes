package com.sypark.finnhub.core.domain.model

import com.sypark.finnhub.core.common.AssetType

data class SearchResult(
    val symbol: String,
    val description: String,
    val assetType: AssetType
)
