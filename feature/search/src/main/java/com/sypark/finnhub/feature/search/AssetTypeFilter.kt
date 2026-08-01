package com.sypark.finnhub.feature.search

import com.sypark.finnhub.core.common.AssetType

enum class AssetTypeFilter(val label: String) {
    ALL("전체"),
    STOCK("주식");

    // "전체" previously matched STOCK-or-FOREX only, silently excluding CRYPTO from every
    // filter including "All" (found while removing FOREX support) -- ALL should mean all.
    fun matches(assetType: AssetType): Boolean = when (this) {
        ALL -> true
        STOCK -> assetType == AssetType.STOCK
    }
}
