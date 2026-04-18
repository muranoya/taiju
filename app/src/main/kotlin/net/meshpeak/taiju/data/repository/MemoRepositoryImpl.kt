package net.meshpeak.taiju.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.data.local.dao.MemoDao
import net.meshpeak.taiju.data.local.entity.MemoEntity
import net.meshpeak.taiju.data.local.entity.toDomain
import net.meshpeak.taiju.data.time.TimeProvider
import net.meshpeak.taiju.domain.model.Memo
import net.meshpeak.taiju.domain.repository.MemoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoRepositoryImpl
    @Inject
    constructor(
        private val dao: MemoDao,
        private val timeProvider: TimeProvider,
    ) : MemoRepository {
        override fun observeByDate(date: LocalDate): Flow<List<Memo>> =
            dao.observeByDate(date).map { list -> list.map(MemoEntity::toDomain) }

        override fun observeRecent(limit: Int): Flow<List<Memo>> = dao.observeRecent(limit).map { list -> list.map(MemoEntity::toDomain) }

        override suspend fun getAll(): List<Memo> = dao.getAll().map(MemoEntity::toDomain)

        override suspend fun append(
            date: LocalDate,
            content: String,
        ) {
            val now = timeProvider.now()
            val next = dao.nextSortOrder(date)
            dao.upsert(
                MemoEntity(
                    date = date,
                    content = content,
                    sortOrder = next,
                    createdAt = now,
                    updatedAt = now,
                ),
            )
        }

        override suspend fun update(
            id: Long,
            content: String,
        ) {
            val entity = dao.findById(id) ?: return
            val now = timeProvider.now()
            dao.upsert(entity.copy(content = content, updatedAt = now))
        }

        override suspend fun delete(id: Long) {
            dao.deleteById(id)
        }

        override suspend fun reorder(orderedIds: List<Long>) {
            if (orderedIds.isEmpty()) return
            dao.reorder(orderedIds, timeProvider.now())
        }

        override suspend fun upsertAll(memos: List<Memo>) {
            if (memos.isEmpty()) return
            val now = timeProvider.now()
            val entities =
                memos.map { m ->
                    MemoEntity(
                        id = 0,
                        date = m.date,
                        content = m.content,
                        sortOrder = m.sortOrder,
                        createdAt = now,
                        updatedAt = now,
                    )
                }
            dao.upsertAll(entities)
        }

        override suspend fun replaceDatesWith(memos: List<Memo>) {
            if (memos.isEmpty()) return
            val now = timeProvider.now()
            memos
                .map { it.date }
                .distinct()
                .forEach { dao.deleteByDate(it) }
            val entities =
                memos.map { m ->
                    MemoEntity(
                        id = 0,
                        date = m.date,
                        content = m.content,
                        sortOrder = m.sortOrder,
                        createdAt = now,
                        updatedAt = now,
                    )
                }
            dao.upsertAll(entities)
        }
    }
