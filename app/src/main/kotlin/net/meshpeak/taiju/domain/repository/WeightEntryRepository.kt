package net.meshpeak.taiju.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.WeightEntry

interface WeightEntryRepository {
    fun observeAll(): Flow<List<WeightEntry>>

    fun observeBetween(
        from: LocalDate,
        to: LocalDate,
    ): Flow<List<WeightEntry>>

    fun observeByDate(date: LocalDate): Flow<WeightEntry?>

    suspend fun findByDate(date: LocalDate): WeightEntry?

    suspend fun findLatest(): WeightEntry?

    suspend fun existingDates(): Set<LocalDate>

    suspend fun upsert(
        date: LocalDate,
        weightKg: Double,
    )

    suspend fun deleteByDate(date: LocalDate)

    suspend fun upsertAll(entries: List<WeightEntry>)
}
