package net.meshpeak.taiju.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.meshpeak.taiju.data.local.entity.MemoEntity
import net.meshpeak.taiju.data.local.entity.WeightEntryEntity

@Database(
    entities = [WeightEntryEntity::class, MemoEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class TaijuDatabase : RoomDatabase()
