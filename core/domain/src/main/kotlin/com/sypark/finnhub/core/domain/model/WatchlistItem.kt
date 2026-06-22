package com.sypark.finnhub.core.domain.model

import com.sypark.finnhub.core.common.AssetType

data class WatchlistItem(
    val symbol: String,
    val displayName: String,
    val assetType: AssetType,
    val sortOrder: Int
)
