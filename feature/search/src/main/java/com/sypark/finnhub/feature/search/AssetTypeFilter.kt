package com.sypark.finnhub.feature.search

import com.sypark.finnhub.core.common.AssetType

enum class AssetTypeFilter(val label: String) {
    ALL("전체"),
    STOCK("주식"),
    FOREX("환율");

    fun matches(assetType: AssetType): Boolean = when (this) {
        ALL -> assetType == AssetType.STOCK || assetType == AssetType.FOREX
        STOCK -> assetType == AssetType.STOCK
        FOREX -> assetType == AssetType.FOREX
    }
}
