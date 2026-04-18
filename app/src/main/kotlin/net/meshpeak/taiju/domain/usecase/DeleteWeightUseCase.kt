package net.meshpeak.taiju.domain.usecase

import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class DeleteWeightUseCase
    @Inject
    constructor(
        private val repo: WeightEntryRepository,
    ) {
        suspend operator fun invoke(date: LocalDate) = repo.deleteByDate(date)
    }
