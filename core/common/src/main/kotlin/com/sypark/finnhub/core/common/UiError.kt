package com.sypark.finnhub.core.common

sealed interface UiError {
    data object Network : UiError
    data object RateLimited : UiError
    data class Api(val code: Int, val message: String) : UiError
    data class Unknown(val message: String) : UiError
}
