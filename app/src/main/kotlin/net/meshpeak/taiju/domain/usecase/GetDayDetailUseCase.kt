package net.meshpeak.taiju.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.DayDetail
import net.meshpeak.taiju.domain.repository.MemoRepository
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject

class GetDayDetailUseCase
    @Inject
    constructor(
        private val weightRepo: WeightEntryRepository,
        private val memoRepo: MemoRepository,
    ) {
        operator fun invoke(date: LocalDate): Flow<DayDetail> =
            combine(
                weightRepo.observeByDate(date),
                memoRepo.observeByDate(date),
            ) { weight, memos -> DayDetail(date, weight, memos) }
    }
