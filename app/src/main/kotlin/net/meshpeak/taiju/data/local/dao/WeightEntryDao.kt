package net.meshpeak.taiju.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import net.meshpeak.taiju.data.local.entity.WeightEntryEntity

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entry ORDER BY date ASC")
    fun observeAll(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entry WHERE date BETWEEN :from AND :to ORDER BY date ASC")
    fun observeBetween(
        from: LocalDate,
        to: LocalDate,
    ): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entry WHERE date = :date LIMIT 1")
    fun observeByDate(date: LocalDate): Flow<WeightEntryEntity?>

    @Query("SELECT * FROM weight_entry WHERE date = :date LIMIT 1")
    suspend fun findByDate(date: LocalDate): WeightEntryEntity?

    @Query("SELECT * FROM weight_entry ORDER BY date DESC LIMIT 1")
    suspend fun findLatest(): WeightEntryEntity?

    @Query("SELECT date FROM weight_entry")
    suspend fun allDates(): List<LocalDate>

    @Upsert
    suspend fun upsert(entry: WeightEntryEntity): Long

    @Upsert
    suspend fun upsertAll(entries: List<WeightEntryEntity>)

    /**
     * date UNIQUE に基づく SQL ベースの UPSERT。
     * Room の `@Upsert` は PrimaryKey (id) でしか UPDATE 対象を引けないため、
     * id を持たない entity が来ると UNIQUE 衝突後の UPDATE が空振りする。
     * 単一日付の保存ではこちらを使う。
     */
    @Query(
        """
        INSERT INTO weight_entry (date, weight_kg, created_at, updated_at)
        VALUES (:date, :weightKg, :createdAt, :updatedAt)
        ON CONFLICT(date) DO UPDATE SET
            weight_kg = excluded.weight_kg,
            updated_at = excluded.updated_at
        """,
    )
    suspend fun upsertByDate(
        date: LocalDate,
        weightKg: Double,
        createdAt: Instant,
        updatedAt: Instant,
    )

    @Query("DELETE FROM weight_entry WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)
}
