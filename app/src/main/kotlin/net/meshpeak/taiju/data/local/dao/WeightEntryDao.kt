package net.meshpeak.taiju.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
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

    @Query("SELECT date FROM weight_entry")
    suspend fun allDates(): List<LocalDate>

    @Upsert
    suspend fun upsert(entry: WeightEntryEntity): Long

    @Upsert
    suspend fun upsertAll(entries: List<WeightEntryEntity>)

    @Query("DELETE FROM weight_entry WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)
}
