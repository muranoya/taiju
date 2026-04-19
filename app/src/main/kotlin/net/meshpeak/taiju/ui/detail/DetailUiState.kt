package net.meshpeak.taiju.ui.detail

import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.ui.home.HomeUiState

data class DetailUiState(
    val date: LocalDate,
    val inputWeightKg: Double = HomeUiState.DEFAULT_WEIGHT_KG,
    val weight: WeightEntry? = null,
    val memos: List<Memo> = emptyList(),
    val editingMemo: Memo? = null,
    val memoInput: String = "",
    val isBottomSheetOpen: Boolean = false,
    val saving: Boolean = false,
)
