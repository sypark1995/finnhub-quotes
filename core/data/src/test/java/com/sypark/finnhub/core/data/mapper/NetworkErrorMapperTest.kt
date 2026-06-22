package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.common.UiError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class NetworkErrorMapperTest {

    private fun httpException(code: Int) = HttpException(
        Response.error<Any>(code, "".toResponseBody("application/json".toMediaType())),
    )

    @Test
    fun `429 maps to RateLimited`() {
        assertEquals(UiError.RateLimited, mapNetworkError(httpException(429)))
    }

    @Test
    fun `other HTTP codes map to Api with the status code`() {
        val error = mapNetworkError(httpException(500))
        assertInstanceOf(UiError.Api::class.java, error)
        assertEquals(500, (error as UiError.Api).code)
    }

    @Test
    fun `IOException maps to Network`() {
        assertEquals(UiError.Network, mapNetworkError(IOException("no connection")))
    }

    @Test
    fun `any other throwable maps to Unknown`() {
        assertInstanceOf(UiError.Unknown::class.java, mapNetworkError(IllegalStateException("boom")))
    }
}
