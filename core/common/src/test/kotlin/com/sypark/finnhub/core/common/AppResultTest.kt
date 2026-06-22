package com.sypark.finnhub.core.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AppResultTest {

    @Test
    fun `getOrNull returns data on Success`() {
        val result: AppResult<Int> = AppResult.Success(42)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `getOrNull returns null on Error`() {
        val result: AppResult<Int> = AppResult.Error(UiError.Network)
        assertNull(result.getOrNull())
    }

    @Test
    fun `map transforms Success data and preserves Error unchanged`() {
        val success: AppResult<Int> = AppResult.Success(2)
        val mapped = success.map { it * 10 }
        assertEquals(AppResult.Success(20), mapped)

        val error: AppResult<Int> = AppResult.Error(UiError.RateLimited)
        val mappedError = error.map { it * 10 }
        assertEquals(AppResult.Error(UiError.RateLimited), mappedError)
    }
}
