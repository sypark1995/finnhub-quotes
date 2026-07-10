package com.sypark.finnhub.core.domain.usecase.alert

import com.sypark.finnhub.core.common.AlertCondition
import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.PriceAlert
import com.sypark.finnhub.core.domain.repository.AlertRepository
import javax.inject.Inject

class CreateAlertUseCase @Inject constructor(
    private val repository: AlertRepository,
) {
    suspend operator fun invoke(symbol: String, targetPrice: Double, condition: AlertCondition): AppResult<Long> =
        repository.create(PriceAlert(id = 0, symbol = symbol, targetPrice = targetPrice, condition = condition, isEnabled = true, triggeredAt = null))
}
