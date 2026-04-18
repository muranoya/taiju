package net.meshpeak.taiju.ui.calendar

import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val year: Int,
    val month: Int,
    val today: LocalDate,
    val weights: Map<LocalDate, Double> = emptyMap(),
)
