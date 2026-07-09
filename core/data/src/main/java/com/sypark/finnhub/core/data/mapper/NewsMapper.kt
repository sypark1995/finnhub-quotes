package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.domain.model.News
import com.sypark.finnhub.core.network.dto.CompanyNewsDto

fun CompanyNewsDto.toDomain(): News = News(
    id = id,
    headline = headline,
    source = source,
    url = url,
    datetime = datetime * 1000,
    summary = summary,
    imageUrl = image,
)
