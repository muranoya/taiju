package net.meshpeak.taiju.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus
import net.meshpeak.taiju.data.time.TimeProvider
import net.meshpeak.taiju.domain.model.Period
import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class GetWeightHistoryUseCase
    @Inject
    constructor(
        private val repo: WeightEntryRepository,
        private val time: TimeProvider,
    ) {
        operator fun invoke(period: Period): Flow<List<WeightEntry>> {
            val today = time.today()
            return when (period) {
                Period.WEEK -> repo.observeBetween(today.minus(DatePeriod(days = 6)), today)
                Period.MONTH -> repo.observeBetween(today.minus(DatePeriod(months = 1)), today)
                Period.QUARTER -> repo.observeBetween(today.minus(DatePeriod(months = 3)), today)
                Period.ALL -> repo.observeAll()
            }
        }
    }
