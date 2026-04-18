package net.meshpeak.taiju.domain.usecase

import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class UpsertWeightUseCase
    @Inject
    constructor(
        private val repo: WeightEntryRepository,
    ) {
        suspend operator fun invoke(
            date: LocalDate,
            weightKg: Double,
        ): Result<Unit> =
            runCatching {
                require(weightKg in WEIGHT_MIN..WEIGHT_MAX) {
                    "weight out of range ($WEIGHT_MIN..$WEIGHT_MAX): $weightKg"
                }
                repo.upsert(date, weightKg)
            }

        companion object {
            const val WEIGHT_MIN = 10.0
            const val WEIGHT_MAX = 300.0
        }
    }
