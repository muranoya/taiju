package net.meshpeak.taiju.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class WeightEntry(
    val id: Long,
    val date: LocalDate,
    val weightKg: Double,
    val createdAt: Instant,
    val updatedAt: Instant,
)
