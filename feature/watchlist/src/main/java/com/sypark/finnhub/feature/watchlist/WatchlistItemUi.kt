package com.sypark.finnhub.feature.watchlist

import com.sypark.finnhub.core.common.AssetType

data class WatchlistItemUi(
    val symbol: String,
    val displayName: String,
    val assetType: AssetType,
)
