package com.sypark.finnhub.core.domain.usecase.alert

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.repository.AlertRepository
import javax.inject.Inject

class MarkAlertTriggeredUseCase @Inject constructor(
    private val repository: AlertRepository,
) {
    suspend operator fun invoke(id: Long): AppResult<Unit> = repository.markTriggered(id)
}
