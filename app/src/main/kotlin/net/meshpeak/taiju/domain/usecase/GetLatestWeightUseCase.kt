package net.meshpeak.taiju.domain.usecase

import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class GetLatestWeightUseCase
    @Inject
    constructor(
        private val repo: WeightEntryRepository,
    ) {
        suspend operator fun invoke(): WeightEntry? = repo.findLatest()
    }
