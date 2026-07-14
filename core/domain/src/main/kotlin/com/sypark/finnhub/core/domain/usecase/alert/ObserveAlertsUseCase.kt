package com.sypark.finnhub.core.domain.usecase.alert

import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAlertsUseCase @Inject constructor(
    private val repository: AlertRepository,
) {
    operator fun invoke(): Flow<List<PriceAlert>> = repository.observeAlerts()
}
