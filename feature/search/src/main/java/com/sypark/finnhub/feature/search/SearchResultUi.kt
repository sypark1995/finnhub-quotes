package com.sypark.finnhub.feature.search

import com.sypark.finnhub.core.common.AssetType

data class SearchResultUi(
    val symbol: String,
    val description: String,
    val assetType: AssetType,
    val isInWatchlist: Boolean,
)
