package net.meshpeak.taiju.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class Converters {
    @TypeConverter
    fun localDateToString(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun instantToEpochMillis(instant: Instant?): Long? = instant?.toEpochMilliseconds()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)
}
