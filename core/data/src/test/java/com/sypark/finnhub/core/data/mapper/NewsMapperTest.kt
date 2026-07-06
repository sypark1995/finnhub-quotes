package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.network.dto.CompanyNewsDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NewsMapperTest {
    @Test
    fun `toDomain converts seconds to millis and keeps every field`() {
        val dto = CompanyNewsDto(id = 1, headline = "Apple announces...", source = "Reuters", url = "https://x", datetime = 1_720_000_000, summary = "...", image = "https://x/i.png")
        val news = dto.toDomain()
        assertEquals(1_720_000_000_000L, news.datetime)
        assertEquals("Apple announces...", news.headline)
    }
}
