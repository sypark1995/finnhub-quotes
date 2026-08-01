package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.common.AssetType
import com.sypark.finnhub.core.domain.model.SearchResult
import com.sypark.finnhub.core.network.dto.SearchResultDto

fun SearchResultDto.toDomain(): SearchResult {
    val assetType = when {
        type.contains("Crypto", ignoreCase = true) -> AssetType.CRYPTO
        else -> AssetType.STOCK
    }
    return SearchResult(symbol = symbol, description = description, assetType = assetType)
}
