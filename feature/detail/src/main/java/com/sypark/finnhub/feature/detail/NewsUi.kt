package com.sypark.finnhub.feature.detail

data class NewsUi(
    val headline: String,
    val source: String,
    val url: String,
    val imageUrl: String,
    val datetime: Long,
)
