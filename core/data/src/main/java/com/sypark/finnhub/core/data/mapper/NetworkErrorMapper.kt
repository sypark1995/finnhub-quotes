package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.common.UiError
import retrofit2.HttpException
import java.io.IOException

fun mapNetworkError(throwable: Throwable): UiError = when (throwable) {
    is HttpException -> if (throwable.code() == 429) {
        UiError.RateLimited
    } else {
        UiError.Api(code = throwable.code(), message = throwable.message() ?: "HTTP ${throwable.code()}")
    }
    is IOException -> UiError.Network
    else -> UiError.Unknown(throwable.message ?: "Unknown error")
}
