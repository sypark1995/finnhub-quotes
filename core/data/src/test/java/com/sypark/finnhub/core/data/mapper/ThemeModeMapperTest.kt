package com.sypark.finnhub.core.data.mapper

import com.sypark.finnhub.core.datastore.StoredThemeMode
import com.sypark.finnhub.core.domain.model.ThemeMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ThemeModeMapperTest {
    @Test
    fun `toDomain and toStored round trip every value`() {
        ThemeMode.entries.forEach { mode -> assertEquals(mode, mode.toStored().toDomain()) }
    }
}
