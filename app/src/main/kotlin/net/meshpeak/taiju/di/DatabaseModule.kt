package net.meshpeak.taiju.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.meshpeak.taiju.data.local.TaijuDatabase
import net.meshpeak.taiju.data.local.dao.MemoDao
import net.meshpeak.taiju.data.local.dao.WeightEntryDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "taiju.db"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): TaijuDatabase =
        Room.databaseBuilder(context, TaijuDatabase::class.java, DATABASE_NAME)
            .build()

    @Provides
    fun provideWeightEntryDao(database: TaijuDatabase): WeightEntryDao = database.weightEntryDao()

    @Provides
    fun provideMemoDao(database: TaijuDatabase): MemoDao = database.memoDao()
}
