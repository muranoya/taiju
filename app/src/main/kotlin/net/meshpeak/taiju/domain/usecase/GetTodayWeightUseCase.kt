package net.meshpeak.taiju.domain.usecase

import kotlinx.coroutines.flow.Flow
import net.meshpeak.taiju.data.time.TimeProvider
import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class GetTodayWeightUseCase
    @Inject
    constructor(
        private val repo: WeightEntryRepository,
        private val time: TimeProvider,
    ) {
        operator fun invoke(): Flow<WeightEntry?> = repo.observeByDate(time.today())
    }
