package net.meshpeak.taiju.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class GetMonthlyWeightsUseCase
    @Inject
    constructor(
        private val repo: WeightEntryRepository,
    ) {
        operator fun invoke(
            year: Int,
            month: Int,
        ): Flow<Map<LocalDate, Double>> {
            val from = LocalDate(year, month, 1)
            val daysInMonth =
                when (month) {
                    1, 3, 5, 7, 8, 10, 12 -> 31
                    4, 6, 9, 11 -> 30
                    2 -> if (isLeapYear(year)) 29 else 28
                    else -> 30
                }
            val to = LocalDate(year, month, daysInMonth)
            return repo.observeBetween(from, to).map { list ->
                list.associate { it.date to it.weightKg }
            }
        }

        private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
