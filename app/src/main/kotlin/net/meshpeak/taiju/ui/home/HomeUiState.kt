package net.meshpeak.taiju.ui.home

import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.model.WeightEntry

data class HomeUiState(
    val today: LocalDate,
    val inputWeight: String = "",
    val todayWeight: WeightEntry? = null,
    val recent: List<WeightEntry> = emptyList(),
    val recentMemos: List<Memo> = emptyList(),
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
)
