package net.meshpeak.taiju.data.csv

import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.model.WeightEntry

data class ParsedBackup(
    val weights: List<WeightEntry>,
    val memos: List<Memo>,
)

data class ImportSummary(
    val importedWeights: Int,
    val importedMemos: Int,
    val conflicts: List<LocalDate>,
)
