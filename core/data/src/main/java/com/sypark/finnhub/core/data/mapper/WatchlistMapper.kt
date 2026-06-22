package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.database.entity.WatchlistEntity
import com.sypark.finnhub.core.domain.model.WatchlistItem

fun WatchlistEntity.toDomain(): WatchlistItem = WatchlistItem(
    symbol = symbol,
    displayName = displayName,
    assetType = assetType,
    sortOrder = sortOrder,
)

fun WatchlistItem.toEntity(addedAt: Long): WatchlistEntity = WatchlistEntity(
    symbol = symbol,
    displayName = displayName,
    assetType = assetType,
    sortOrder = sortOrder,
    addedAt = addedAt,
)
