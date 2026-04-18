package net.meshpeak.taiju.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.domain.model.Memo

interface MemoRepository {
    fun observeByDate(date: LocalDate): Flow<List<Memo>>

    fun observeRecent(limit: Int): Flow<List<Memo>>

    suspend fun getAll(): List<Memo>

    suspend fun append(
        date: LocalDate,
        content: String,
    )

    suspend fun update(
        id: Long,
        content: String,
    )

    suspend fun delete(id: Long)

    suspend fun reorder(orderedIds: List<Long>)

    suspend fun upsertAll(memos: List<Memo>)

    suspend fun replaceDatesWith(memos: List<Memo>)
}
