package net.meshpeak.taiju.ui.chart

import net.meshpeak.taiju.domain.model.Period
import net.meshpeak.taiju.domain.model.WeightEntry

data class ChartUiState(
    val period: Period = Period.MONTH,
    val points: List<WeightEntry> = emptyList(),
    val targetWeightKg: Double? = null,
)
