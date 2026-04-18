package net.meshpeak.taiju.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Memo(
    val id: Long,
    val date: LocalDate,
    val content: String,
    val sortOrder: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
)
