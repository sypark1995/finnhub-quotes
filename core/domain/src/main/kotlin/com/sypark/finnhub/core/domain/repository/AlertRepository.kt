package com.sypark.finnhub.core.domain.repository

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.PriceAlert
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun observeAlerts(): Flow<List<PriceAlert>>
    fun observeAlertsForSymbol(symbol: String): Flow<List<PriceAlert>>
    suspend fun create(alert: PriceAlert): AppResult<Long>
    suspend fun update(alert: PriceAlert): AppResult<Unit>
    suspend fun delete(id: Long): AppResult<Unit>
    suspend fun markTriggered(id: Long): AppResult<Unit>
}
