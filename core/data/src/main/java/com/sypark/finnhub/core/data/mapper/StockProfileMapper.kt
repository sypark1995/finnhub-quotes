package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.domain.model.StockProfile
import com.sypark.finnhub.core.network.dto.StockProfileDto

fun StockProfileDto.toDomain(symbol: String): StockProfile = StockProfile(
    symbol = symbol,
    name = name,
    exchange = exchange,
    industry = finnhubIndustry,
    logoUrl = logo,
    marketCapitalization = marketCapitalization,
    webUrl = weburl,
    currency = currency,
)
