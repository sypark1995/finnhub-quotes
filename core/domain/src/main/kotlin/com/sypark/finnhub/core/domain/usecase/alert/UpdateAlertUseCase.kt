package com.sypark.finnhub.core.domain.usecase.alert

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.repository.AlertRepository
import javax.inject.Inject

class UpdateAlertUseCase @Inject constructor(
    private val repository: AlertRepository,
) {
    suspend operator fun invoke(alert: PriceAlert): AppResult<Unit> = repository.update(alert)
}
