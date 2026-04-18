package net.meshpeak.taiju.ui.detail

import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.model.WeightEntry

data class DetailUiState(
    val date: LocalDate,
    val weightInput: String = "",
    val weight: WeightEntry? = null,
    val memos: List<Memo> = emptyList(),
    val editingMemo: Memo? = null,
    val memoInput: String = "",
    val isBottomSheetOpen: Boolean = false,
    val errorMessage: String? = null,
    val saving: Boolean = false,
)
