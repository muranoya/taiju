package net.meshpeak.taiju.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.data.local.dao.WeightEntryDao
import net.meshpeak.taiju.data.local.entity.WeightEntryEntity
import net.meshpeak.taiju.data.local.entity.toDomain
import net.meshpeak.taiju.data.local.entity.toEntity
import net.meshpeak.taiju.data.time.TimeProvider
import net.meshpeak.taiju.domain.model.WeightEntry
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightEntryRepositoryImpl
    @Inject
    constructor(
        private val dao: WeightEntryDao,
        private val timeProvider: TimeProvider,
    ) : WeightEntryRepository {
        override fun observeAll(): Flow<List<WeightEntry>> = dao.observeAll().map { list -> list.map(WeightEntryEntity::toDomain) }

        override fun observeBetween(
            from: LocalDate,
            to: LocalDate,
        ): Flow<List<WeightEntry>> = dao.observeBetween(from, to).map { list -> list.map(WeightEntryEntity::toDomain) }

        override fun observeByDate(date: LocalDate): Flow<WeightEntry?> = dao.observeByDate(date).map { it?.toDomain() }

        override suspend fun findByDate(date: LocalDate): WeightEntry? = dao.findByDate(date)?.toDomain()

        override suspend fun findLatest(): WeightEntry? = dao.findLatest()?.toDomain()

        override suspend fun existingDates(): Set<LocalDate> = dao.allDates().toSet()

        override suspend fun upsert(
            date: LocalDate,
            weightKg: Double,
        ) {
            val now = timeProvider.now()
            dao.upsertByDate(
                date = date,
                weightKg = weightKg,
                createdAt = now,
                updatedAt = now,
            )
        }

        override suspend fun deleteByDate(date: LocalDate) {
            dao.deleteByDate(date)
        }

        override suspend fun upsertAll(entries: List<WeightEntry>) {
            if (entries.isEmpty()) return
            val now = timeProvider.now()
            val entities =
                entries.map { domain ->
                    val existing = dao.findByDate(domain.date)
                    if (existing != null) {
                        existing.copy(weightKg = domain.weightKg, updatedAt = now)
                    } else {
                        domain.toEntity().copy(id = 0, createdAt = now, updatedAt = now)
                    }
                }
            dao.upsertAll(entities)
        }
    }
