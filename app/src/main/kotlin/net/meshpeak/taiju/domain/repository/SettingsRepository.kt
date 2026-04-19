package net.meshpeak.taiju.domain.repository

import kotlinx.coroutines.flow.Flow
import net.meshpeak.taiju.domain.model.AppTheme
import net.meshpeak.taiju.domain.model.UserSettings

interface SettingsRepository {
    val settings: Flow<UserSettings>

    suspend fun setTheme(theme: AppTheme)

    suspend fun setTargetWeight(kg: Double?)
}
