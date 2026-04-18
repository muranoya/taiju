package net.meshpeak.taiju.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.meshpeak.taiju.data.repository.MemoRepositoryImpl
import net.meshpeak.taiju.data.repository.SettingsRepositoryImpl
import net.meshpeak.taiju.data.repository.WeightEntryRepositoryImpl
import net.meshpeak.taiju.domain.repository.MemoRepository
import net.meshpeak.taiju.domain.repository.SettingsRepository
import net.meshpeak.taiju.domain.repository.WeightEntryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWeightEntryRepository(impl: WeightEntryRepositoryImpl): WeightEntryRepository

    @Binds
    @Singleton
    abstract fun bindMemoRepository(impl: MemoRepositoryImpl): MemoRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
