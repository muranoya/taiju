package net.meshpeak.taiju.ui.home

import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.WeightEntry

data class HomeUiState(
    val today: LocalDate,
    val inputWeightKg: Double = DEFAULT_WEIGHT_KG,
    val todayWeight: WeightEntry? = null,
    val recent: List<WeightEntry> = emptyList(),
    val isSaving: Boolean = false,
) {
    companion object {
        const val DEFAULT_WEIGHT_KG: Double = 60.0
    }
}
