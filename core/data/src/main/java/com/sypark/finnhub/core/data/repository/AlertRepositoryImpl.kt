package com.sypark.finnhub.core.data.repository

import com.sypark.finnhub.core.common.AppDispatchers
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.common.UiError
import com.sypark.finnhub.core.data.mapper.toDomain
import com.sypark.finnhub.core.data.mapper.toEntity
import com.sypark.finnhub.core.database.dao.PriceAlertDao
import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val dao: PriceAlertDao,
    private val dispatchers: AppDispatchers,
) : AlertRepository {

    override fun observeAlerts(): Flow<List<PriceAlert>> = dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeAlertsForSymbol(symbol: String): Flow<List<PriceAlert>> =
        dao.observeForSymbol(symbol).map { entities -> entities.map { it.toDomain() } }

    override suspend fun create(alert: PriceAlert): AppResult<Long> = withContext(dispatchers.io) {
        runCatching { dao.insert(alert.toEntity(createdAt = System.currentTimeMillis())) }
            .fold({ AppResult.Success(it) }, { AppResult.Error(UiError.Unknown(it.message ?: "create failed")) })
    }

    override suspend fun update(alert: PriceAlert): AppResult<Unit> = withContext(dispatchers.io) {
        runCatching { dao.update(alert.id, alert.targetPrice, alert.condition, alert.isEnabled) }
            .fold({ AppResult.Success(Unit) }, { AppResult.Error(UiError.Unknown(it.message ?: "update failed")) })
    }

    override suspend fun delete(id: Long): AppResult<Unit> = withContext(dispatchers.io) {
        runCatching { dao.delete(id) }
            .fold({ AppResult.Success(Unit) }, { AppResult.Error(UiError.Unknown(it.message ?: "delete failed")) })
    }

    override suspend fun markTriggered(id: Long): AppResult<Unit> = withContext(dispatchers.io) {
        runCatching { dao.markTriggered(id, System.currentTimeMillis()) }
            .fold({ AppResult.Success(Unit) }, { AppResult.Error(UiError.Unknown(it.message ?: "markTriggered failed")) })
    }
}
