package net.meshpeak.taiju.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.data.local.entity.MemoEntity

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo WHERE date = :date ORDER BY sort_order ASC, id ASC")
    fun observeByDate(date: LocalDate): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memo ORDER BY date DESC, sort_order ASC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memo ORDER BY date ASC, sort_order ASC")
    suspend fun getAll(): List<MemoEntity>

    @Query("SELECT * FROM memo WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): MemoEntity?

    @Query("SELECT COALESCE(MAX(sort_order), -1) + 1 FROM memo WHERE date = :date")
    suspend fun nextSortOrder(date: LocalDate): Int

    @Upsert
    suspend fun upsert(memo: MemoEntity): Long

    @Upsert
    suspend fun upsertAll(memos: List<MemoEntity>)

    @Query("DELETE FROM memo WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM memo WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)

    @Query("UPDATE memo SET sort_order = :sortOrder, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateSortOrder(
        id: Long,
        sortOrder: Int,
        updatedAt: Instant,
    )

    @Transaction
    suspend fun reorder(
        orderedIds: List<Long>,
        now: Instant,
    ) {
        orderedIds.forEachIndexed { index, id -> updateSortOrder(id, index, now) }
    }
}
