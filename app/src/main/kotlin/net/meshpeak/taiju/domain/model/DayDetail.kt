package net.meshpeak.taiju.domain.model

import kotlinx.datetime.LocalDate

data class DayDetail(
    val date: LocalDate,
    val weight: WeightEntry?,
    val memos: List<Memo>,
)
