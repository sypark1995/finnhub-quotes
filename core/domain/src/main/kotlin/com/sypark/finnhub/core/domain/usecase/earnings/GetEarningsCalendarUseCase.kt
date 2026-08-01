package com.sypark.finnhub.core.domain.usecase.earnings

import com.sypark.finnhub.core.common.AppResult
import com.sypark.finnhub.core.domain.model.EarningsEvent
import com.sypark.finnhub.core.domain.repository.MarketRepository
import javax.inject.Inject

class GetEarningsCalendarUseCase @Inject constructor(
    private val repository: MarketRepository,
) {
    suspend operator fun invoke(from: String, to: String, symbol: String? = null): AppResult<List<EarningsEvent>> =
        repository.getEarningsCalendar(from, to, symbol)
}
